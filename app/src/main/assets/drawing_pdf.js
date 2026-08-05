RENDER["drawings"]=function(){
var list="";var drawings=D.drawings||[];
if(drawings.length===0){list='<p style="color:#666">No drawings registered.</p>';}
else{for(var i=0;i<drawings.length;i++){var d=drawings[i];var pdfBtn=d.fileData?' <button class="btn btn-sm btn-blue" onclick="openPDF(\''+d.id+'\')">Open PDF</button>':'';list+='<div class="card"><div style="display:flex;justify-content:space-between"><h4>'+d.number+'</h4><button class="btn btn-red btn-sm" onclick="delItem(\'drawings\',\''+d.id+'\');go(\'drawings\')">X</button></div><p>'+d.title+'</p><p style="font-size:12px;color:#aaa">Rev: '+d.revision+' | '+d.discipline+' | Received: '+d.dateReceived+pdfBtn+'</p></div>';}}
return '<h2>Drawings Register</h2>'+list+'<button class="btn btn-blue" onclick="showDrawingForm()">+ Add Drawing</button>';
};
function openPDF(id){
var d=D.drawings.find(function(x){return x.id===id;});
if(d&&d.fileData){
var w=window.open("","_blank","width=900,height=700");
w.document.write('<html><body style="margin:0"><iframe src="'+d.fileData+'" width="100%" height="100%" style="border:none"></iframe></body></html>');
w.document.close();
}
}
function showDrawingForm(){
var h='<h3>Add Drawing</h3><label>Drawing Number:</label><input id="_dwgNo" placeholder="e.g. STR/001"><label>Title:</label><input id="_dwgTitle" placeholder="Drawing title"><label>Discipline:</label><select id="_dwgDisc"><option>Structural</option><option>Architectural</option><option>Civil</option><option>Mechanical</option><option>Electrical</option><option>Plumbing</option><option>Fire Services</option><option>Landscape</option></select><label>Revision:</label><input id="_dwgRev" placeholder="A"><label>Date Received:</label><input type="date" id="_dwgDate" value="'+globalDate+'"><label>Attach PDF:</label><input type="file" id="_dwgFile" accept=".pdf" onchange="readDwgPDF(this)"><input type="hidden" id="_dwgFileData"><small style="color:#aaa;font-size:10px">Select a PDF file to attach (max 10MB)</small><label>Notes:</label><textarea id="_dwgNotes"></textarea><button class="btn" onclick="saveDrawing()">Save Drawing</button><button class="btn btn-secondary" onclick="go(\'drawings\')">Cancel</button>';
document.getElementById("content").innerHTML=h;
}
function readDwgPDF(input){
var file=input.files[0];
if(file&&file.size<10000000){
var reader=new FileReader();
reader.onload=function(e){document.getElementById("_dwgFileData").value=e.target.result;};
reader.readAsDataURL(file);
}else{toast("File too large (max 10MB)");}
}
function saveDrawing(){
if(!D.drawings)D.drawings=[];
var fd=document.getElementById("_dwgFileData").value;
D.drawings.push({id:uid(),number:document.getElementById("_dwgNo").value,title:document.getElementById("_dwgTitle").value,discipline:document.getElementById("_dwgDisc").value,revision:document.getElementById("_dwgRev").value,dateReceived:document.getElementById("_dwgDate").value,fileData:fd||null,notes:document.getElementById("_dwgNotes").value,timestamp:Date.now()});
save();toast("Drawing saved!");go("drawings");
}
