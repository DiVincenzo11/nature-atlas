"""Download/verify the official CUB-200-2011 archive; do not redistribute images."""
import argparse,hashlib,json,shutil,tarfile,time
from pathlib import Path
import httpx

RECORD='https://data.caltech.edu/api/records/65de6-vp158'
MD5='97eceeb196236b17998738112f37df78'


def download(out):
    out=Path(out).resolve();out.mkdir(parents=True,exist_ok=True)
    archive=out/'CUB_200_2011.tgz'
    with httpx.Client(timeout=120,follow_redirects=True) as client:
        response=client.get(RECORD);response.raise_for_status()
        item=response.json()['files']['entries']['CUB_200_2011.tgz']
        if item['checksum']!='md5:'+MD5:raise RuntimeError('Official checksum changed; inspect source')
        if not archive.exists():
            partial=archive.with_suffix('.tgz.part');size=0;last=time.monotonic()
            if partial.exists():raise RuntimeError('Partial download exists; inspect before retrying')
            with client.stream('GET',item['links']['content']) as response:
                response.raise_for_status()
                with partial.open('xb') as stream:
                    for block in response.iter_bytes(4*1024*1024):
                        stream.write(block);size+=len(block)
                        if time.monotonic()-last>=15:
                            print(json.dumps({'downloaded_bytes':size,'total_bytes':item['size']}),flush=True);last=time.monotonic()
            if size!=item['size']:raise RuntimeError('Incomplete archive')
            partial.replace(archive)
    md5=hashlib.md5();sha=hashlib.sha256()
    with archive.open('rb') as stream:
        for block in iter(lambda:stream.read(4*1024*1024),b''):md5.update(block);sha.update(block)
    if md5.hexdigest()!=MD5:raise RuntimeError('Official archive checksum mismatch')
    destination=out/'CUB_200_2011'
    if destination.exists():raise RuntimeError('Extracted directory already exists; refusing overwrite')
    files=0
    with tarfile.open(archive,'r:gz') as tar:
        for member in tar:
            if not member.name.startswith('CUB_200_2011/'):continue
            path=(out/member.name).resolve()
            if not path.is_relative_to(destination) or '\\' in member.name or not (member.isdir() or member.isfile()):
                raise RuntimeError('Unexpected archive member')
            if member.isdir():path.mkdir(parents=True,exist_ok=True);continue
            path.parent.mkdir(parents=True,exist_ok=True)
            with tar.extractfile(member) as source,path.open('xb') as target:shutil.copyfileobj(source,target,1024*1024)
            files+=1
    provenance={'dataset':'CUB-200-2011','record':RECORD,'homepage':'https://www.vision.caltech.edu/datasets/cub_200_2011/',
                'archive':archive.name,'bytes':archive.stat().st_size,'md5':md5.hexdigest(),'sha256':sha.hexdigest(),'extracted_files':files,
                'image_use':'Non-commercial research and education; original image copyrights remain with owners.',
                'pretraining_warning':'Official dataset warns that CUB images may overlap ImageNet pretraining data.'}
    (out/'dataset-source.json').write_text(json.dumps(provenance,indent=2)+'\n',encoding='utf-8')
    print(json.dumps(provenance),flush=True)


if __name__=='__main__':
    parser=argparse.ArgumentParser();parser.add_argument('--out',type=Path,required=True)
    download(parser.parse_args().out)
