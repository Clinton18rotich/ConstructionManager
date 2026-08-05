// ============ PIN LOCK ============
var APP_PIN=D.pin||"";

function checkPIN(){
if(APP_PIN&&APP_PIN.length>0){
var entered=prompt("Enter PIN to access Construction Manager:");
if(entered!==APP_PIN){
if(entered!==null){
for(var i=0;i<2;i++){
var retry=prompt("Wrong PIN. "+(2-i)+" attempts remaining:");
if(retry===APP_PIN)return true;
}
document.body.innerHTML='<div style="display:flex;align-items:center;justify-content:center;height:100vh;background:#0a0a0a;color:#fff;text-align:center"><div><h1 style="color:#ff3b30">Access Denied</h1><p>Too many failed attempts.</p><p style="color:#aaa">Close and reopen the app to try again.</p></div></div>';
return false;
}
return false;
}
}
return true;
}

function setPIN(){
var p=prompt("Enter new 4-digit PIN:");
if(p&&p.length>=4&&/^\d+$/.test(p)){
var c=prompt("Confirm PIN:");
if(p===c){
D.pin=p;APP_PIN=p;save();
toast("PIN set successfully!");
}else{toast("PINs do not match");}
}else if(p){toast("PIN must be at least 4 digits");}
}

function removePIN(){
var p=prompt("Enter current PIN to remove:");
if(p===APP_PIN){
D.pin="";APP_PIN="";save();
toast("PIN removed");
}else{toast("Wrong PIN");}
}
