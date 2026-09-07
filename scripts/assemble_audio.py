from pathlib import Path
import hashlib,json
root=Path(__file__).resolve().parents[1]
source=root/'audio-parts';manifest=json.loads((source/'manifest.json').read_text());target=root/'app/src/main/assets/audio';target.mkdir(parents=True,exist_ok=True)
h=hashlib.sha256();total=0
with (target/'playlist.m4a').open('wb') as out:
 for part in manifest['parts']:
  data=(source/part['file']).read_bytes()
  assert len(data)==part['bytes'] and hashlib.sha256(data).hexdigest()==part['sha256'],part['file']
  out.write(data);h.update(data);total+=len(data)
assert total==manifest['bytes'] and h.hexdigest()==manifest['sha256']
print('Full playlist assembled and SHA256 verified:',total,'bytes')
