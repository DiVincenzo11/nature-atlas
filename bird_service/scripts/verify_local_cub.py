"""Check a running Python -> authenticated Java adapter using fixed CUB IDs.

Uses only the disposable local demo account and database. Does not save photos
or tokens. Run after starting the services documented in the README.
"""
import argparse
import io
import json
import time
from pathlib import Path
import httpx
from PIL import Image


def main():
    p=argparse.ArgumentParser();p.add_argument('--manifest',type=Path,required=True)
    p.add_argument('--out',type=Path,required=True)
    a=p.parse_args()
    if a.out.exists():raise FileExistsError(a.out)
    rows=[json.loads(l) for l in a.manifest.read_text(encoding='utf-8').splitlines() if l.strip()]
    cases=[r for r in rows if r['split']=='test'][:3]
    checks=[]
    with httpx.Client(timeout=60,trust_env=False) as client:
        health=client.get('http://127.0.0.1:18902/health').raise_for_status().json()
        assert health['ready'] and health['model']['class_count']==200 and health['model']['mode']=='custom_species'
        login=client.post('http://127.0.0.1:18900/user/login',json={'username':'demo','password':'Demo123456'}).raise_for_status().json()
        assert login['code']==0
        user,token=login['data'];client.headers.update({'token':token,'Authorization':'Bearer '+token})
        for row in cases:
            # Respect the existing 3-second application rate limit.
            time.sleep(3.2)
            image=a.manifest.parent/row['file']
            response=client.post('http://127.0.0.1:18900/detect/birdpic',data={'userid':str(user['userId'])},
                files={'file':(image.name,image.read_bytes(),'image/jpeg')}).raise_for_status().json()
            assert response['code']==0,response
            data=response['data']
            assert data['model']['classifier_sha256']==health['model']['classifier_sha256']
            checks.append({'image_id':row['image_id'],'target':row['label'],'status':data['status'],
                'detections':data['detections'],'model':data['model'],'info_list':data['info_list'],
                'annotated_image_returned':data['detect_img_url'].startswith('data:image/jpeg;base64,')})
        blank=io.BytesIO();Image.new('RGB',(640,480),'white').save(blank,format='PNG');time.sleep(3.2)
        response=client.post('http://127.0.0.1:18900/detect/birdpic',data={'userid':str(user['userId'])},
            files={'file':('blank.png',blank.getvalue(),'image/png')}).raise_for_status().json()
        assert response['code']==0 and response['data']['status']=='no_bird_detected' and response['data']['detections']==[]
        invalid=client.post('http://127.0.0.1:18902/predict',files={'file':('invalid.jpg',b'not an image','image/jpeg')})
        assert invalid.status_code==400
    report={'model_health':health,'python_to_java_authenticated_upload':checks,
        'blank_image_no_fabricated_detections':True,'invalid_upload_http400':True,
        'scope':'Fixed first three test images; integration check, not accuracy estimate.'}
    a.out.parent.mkdir(parents=True,exist_ok=True)
    a.out.write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
    print(json.dumps({'api_cases':len(checks),'class_count':200,'blank':True,'invalid_http400':True}))


if __name__=='__main__':main()
