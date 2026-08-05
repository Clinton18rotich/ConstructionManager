// ============ PHOTO ATTACHMENTS ============
var photoData=[];

function capturePhoto(section,entryId){
var input=document.createElement("input");
input.type="file";
input.accept="image/*";
input.capture="environment";
input.onchange=function(e){
var file=e.target.files[0];
if(file&&file.size<5000000){
var reader=new FileReader();
reader.onload=function(ev){
if(!D.photos)D.photos=[];
D.photos.push({id:uid(),section:section,entryId:entryId||"",date:globalDate,data:ev.target.result,timestamp:Date.now()});
save();toast("Photo saved!");
go(section);
};
reader.readAsDataURL(file);
}else{toast("Photo too large (max 5MB)");}
};
input.click();
}

function showPhotos(section,entryId){
var photos=(D.photos||[]).filter(function(p){
if(entryId)return p.section===section&&p.entryId===entryId;
return p.section===section&&p.date===globalDate;
});
var h='<div style="display:flex;flex-wrap:wrap;gap:4px">';
if(photos.length===0){h+='<p style="color:#666">No photos</p>';}
else{for(var i=0;i<photos.length;i++){h+='<div style="position:relative"><img src="'+photos[i].data+'" style="width:80px;height:80px;object-fit:cover;border-radius:4px" onclick="viewPhoto(\''+photos[i].id+'\')"><button class="btn btn-red btn-sm" style="position:absolute;top:0;right:0;padding:2px 6px;font-size:10px" onclick="delItem(\'photos\',\''+photos[i].id+'\');go(\''+section+'\')">X</button></div>';}}
h+='</div>';
return h;
}

function viewPhoto(id){
var p=(D.photos||[]).find(function(x){return x.id===id;});
if(p){
var w=window.open("","_blank","width=500,height=600");
w.document.write('<html><body style="margin:0;background:#000;display:flex;align-items:center;justify-content:center"><img src="'+p.data+'" style="max-width:100%;max-height:100vh"></body></html>');
w.document.close();
}
}
