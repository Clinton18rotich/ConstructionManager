// ============ KENYAN WEATHER SERVICE ============
function getKenyanWeather(){
var m=new Date(globalDate).getMonth();
if(m>=2&&m<=4)return {morning:"Rainy",afternoon:"Rainy",temp:"18-26C",ground:"Muddy"};
if(m>=9&&m<=11)return {morning:"Rainy",afternoon:"Cloudy",temp:"19-27C",ground:"Muddy"};
if(m>=5&&m<=8)return {morning:"Cloudy",afternoon:"Sunny",temp:"15-24C",ground:"Dry"};
return {morning:"Sunny",afternoon:"Sunny",temp:"20-30C",ground:"Dry"};
}
function autoWeather(){
var w=getKenyanWeather();
if(!D.weatherMorning||D.weatherMorning==="")D.weatherMorning=w.morning;
if(!D.weatherAfternoon||D.weatherAfternoon==="")D.weatherAfternoon=w.afternoon;
if(!D.tempNotes||D.tempNotes==="")D.tempNotes=w.temp;
if(!D.groundCondition||D.groundCondition==="")D.groundCondition=w.ground;
save();
}
