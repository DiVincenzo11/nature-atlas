"""Run only against the disposable local demonstration database."""
import io,json,os,time,uuid
import requests,websocket
BASE=os.getenv('BASE_URL','http://127.0.0.1:18900')
checks=[]
def call(s,method,path,**kw):
 r=s.request(method,BASE+path,timeout=20,**kw);r.encoding='utf-8';return r
def ok(s,method,path,**kw):
 r=call(s,method,path,**kw);r.raise_for_status();j=r.json();assert j['code']==0,j;return j['data']
def passed(name):checks.append(name);print('PASS',name)
def login(name='demo'):
 s=requests.Session();user,token=ok(s,'POST','/user/login',json={'username':name,'password':'Demo123456'})
 assert user.get('password') is None
 s.headers.update({'token':token,'Authorization':'Bearer '+token});return s,user,token
s=requests.Session();tree=ok(s,'GET','/bird/allbirddata');assert tree;passed('bird taxonomy tree')
allrows=ok(s,'POST','/bird/birdchar',json={'mouth':'','crest':'','neck':'','back':'','leg':''});assert len(allrows)==8
filtered=ok(s,'POST','/bird/birdchar',json={'mouth':'黄褐色','crest':'','neck':'','back':'','leg':''});assert 0<len(filtered)<len(allrows)
assert ok(s,'POST','/bird/birdchar',json={'mouth':"' OR 1=1 --",'crest':'','neck':'','back':'','leg':''})==[]
passed('feature filtering / parameterized query')
name=allrows[0][0];detail=ok(s,'GET','/bird/birdDetail',params={'name':name});assert detail['name']==name and detail['picurls'];passed('bird detail / image list')
result=ok(s,'GET','/search',params={'key':name});assert result and result[0]['name']==name;assert len(ok(s,'GET','/getAll'))>=8;passed('cross-category search')
for category,path in [('cat','allcatdata'),('dog','alldogdata'),('turtle','allturtledata'),('liangqi','allliangqidata')]:assert ok(s,'GET','/'+category+'/'+path)
passed('cat / dog / reptile / amphibian atlas queries')
bad=call(s,'POST','/user/login',json={'username':'demo','password':'wrong'}).json();assert bad['code']!=0;passed('invalid login rejected')
user,userinfo,token=login();assert userinfo['userId']==1;passed('login / JWT / password hash excluded from response')
assert call(s,'GET','/detect/log',params={'userid':1}).status_code==401
assert call(user,'GET','/detect/log',params={'userid':2}).status_code==403
assert call(user,'POST','/user/updateNickname',json={'username':'expert_demo','nickname':'forbidden'}).json()['code']!=0
ok(user,'POST','/user/updateNickname',json={'username':'demo','nickname':'演示观察员'});passed('authenticated profile / ownership and history access checks')
assert call(s,'POST','/detect/birdpic',data={'userid':1},files={'file':('demo.png',b'test','image/png')}).status_code==401
j=call(user,'POST','/detect/birdpic',data={'userid':1},files={'file':('demo.png',b'test','image/png')}).json();assert j['code']!=0 and '未配置' in j['mseeage'];passed('unconfigured image recognition returns explicit failure, no fabricated predictions')
experts=ok(user,'GET','/expert/list');assert experts and experts[0]['expertId']==1
history=ok(user,'GET','/expert/messages',params={'expertId':1});assert history;passed('expert directory / isolated message history')
ws=websocket.create_connection(BASE.replace('http','ws',1)+'/ws-chat/websocket',timeout=8,origin='http://127.0.0.1:18901')
try:
 ws.send('CONNECT\naccept-version:1.2\nhost:localhost\nAuthorization:Bearer '+token+'\n\n\x00')
 assert 'CONNECTED' in ws.recv()
 ws.send('SUBSCRIBE\nid:smoke\ndestination:/user/queue/expert/chat\n\n\x00')
 marker='演示回归-'+uuid.uuid4().hex[:8]
 ws.send('SEND\ndestination:/app/expert/send\ncontent-type:application/json\n\n'+json.dumps({'expertId':1,'content':marker,'contentType':'TEXT'},ensure_ascii=False)+'\x00')
 msg=ws.recv();assert 'MESSAGE' in msg and marker in msg,msg
 rows=ok(user,'GET','/expert/messages',params={'expertId':1});assert any(x['content']==marker for x in rows)
 passed('STOMP authenticated send / private subscription / database persistence')
finally:ws.close()
new,_,_=login();assert call(user,'GET','/detect/log',params={'userid':1}).status_code==401;passed('second login invalidates previous session')
print(json.dumps({'passed':len(checks),'checks':checks},ensure_ascii=False,indent=2))
