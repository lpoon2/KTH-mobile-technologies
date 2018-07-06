
var today = new Date();
var dd = today.getDate();
var mm = today.getMonth()+1; //January is 0!
var temp= 0;
var cd = 0;
var hud =0;
var yyyy = today.getFullYear();
if(dd<10){
    dd='0'+dd;
}
if(mm<10){
    mm='0'+mm;
}
var today = dd+'/'+mm+'/'+yyyy;

var dateToBlock = function(date){
  var day = date.split('-')[2].substring(0,2);
  var ddd = parseInt(dd);
  
  if(parseInt(day) === ((30+ddd)%30)){
    return 'today';
  }
  else if(parseInt(day) === ((30+ddd-1)%30 )){
    return 'yesterday';
  }
  else if(parseInt(day) === ((30+ddd-2)%30) ){
    return 'two days ago';
  }
  return 'three days ago';
}
var datetoPosition = function(date){
  var d = date.split('-');
  var time = d[2].substring(3,11);
  time = time.split(':')
  d[2] = d[2].substring(0,2);
  //var interval = ((199499999)/4);
  //var base = interval*(parseInt(d[2])%26);
  var base = 0;
  var interval = 247999999;
  return base + interval*(parseInt(time[0])/24) + interval*(parseInt(time[1])/60) + interval*(parseInt(time[2])/3600);
  //return str(pos)
}

var width = document.getElementsByClassName('mdl-card__supporting-text')[0].offsetWidth
var circosLine = new Circos({
  container: '#lineChart',
  width: width,
  height: width
})

var gieStainColor = {
  gpos100: 'rgb(0,0,0)',
  gpos: 'rgb(0,0,0)',
  gpos75: 'rgb(130,130,130)',
  gpos66: 'rgb(160,160,160)',
  gpos50: 'rgb(200,200,200)',
  gpos33: 'rgb(210,210,210)',
  gpos25: 'rgb(200,200,200)',
  gvar: 'rgb(220,220,220)',
  gneg: 'rgb(255,255,255)',
  acen: 'rgb(217,47,39)',
  stalk: 'rgb(100,127,164)',
  select: 'rgb(135,177,255)'
}

var drawCircos = function (error, GRCh37, snp250, snp, snp1m) {
  GRCh37 = GRCh37.filter(function (d) {
    return d.id === 'three days ago' || d.id === 'two days ago' || d.id === 'yesterday' || d.id === 'today'
  })
  temp= 0;
  cd = 0;
  hud =0;
  snp250 = snp250.map(function (d) {
    temp+= parseInt(d.t);
    return {
      block_id: dateToBlock(d.timestamp),
      position: datetoPosition(d.timestamp),
      value: d.t
    }
  })
  temp = temp/snp250.length;

  snp = snp.map(function (d) {
    cd += parseInt(d.c);
    return {
      block_id: dateToBlock(d.timestamp),
      position: datetoPosition(d.timestamp),
      value: d.c
    }
  })
  cd = cd / snp.length ;

  snp1m = snp1m.map(function (d) {
    hud += parseInt(d.h);
    return {
      block_id: dateToBlock(d.timestamp),
      position: datetoPosition(d.timestamp),
      value: d.h
    }
  })
  hud = hud/ snp1m.length;

  circosLine
    .layout(
      GRCh37,
    {
      innerRadius: width/2 - 100,
      outerRadius: width/2 - 80,
      labels: {display: true},
      ticks: {display: true}
    }
    )
    .line('snp-250', snp250, {
      innerRadius: 0.5,
      outerRadius: 0.8,
      maxGap: 1000000,
      color: '#ba1668',
      labels: {
        display: true,
        position: 'center',
        labelSize: '20px'
      },
      axes: [
        {
          spacing: 0.001,
          thickness: 1,
          color: '#666666'
        }
      ],
      backgrounds: [
        {
          start: 0,
          end: 0.002,
          color: '#f44336',
          opacity: 0.5
        },
        {
          start: 0.006,
          end: 0.015,
          color: '#4caf50',
          opacity: 0.5
        }
      ],
      tooltipContent: function (datum, index) {
    return '<h5> Temperature '+'➤ 21.3 </h5> <i>(CTRL+C to copy to clipboard)</i>'
  }
    })
    .line('snp', snp, {
      innerRadius: 1.01,
      outerRadius: 1.15,
      maxGap: 1000000,
      // min: 0,
      // max: 0.015,
      color: '#222222',
      labels: {
        display: true,
        position: 'center',
        labelSize: '60px'
      },
      axes: [
        {
          position: 0.002,
          color: '#f44336'
        },
        {
          position: 0.006,
          color: '#4caf50'
        }
      ],
      tooltipContent: function (datum, index) {
    return '<h5> Carbon dioxide '+'➤ 423 </h5> <i>(CTRL+C to copy to clipboard)</i>'
  }
    })
    .line('snp1m', snp1m, {
      innerRadius: 0.8,
      outerRadius: 0.95,
      maxGap: 1000000,
      color: '#1baa16',
      labels: {
        display: true,
        position: 'center',
        labelSize: '20px'
      },
      tooltipContent: function (datum, index) {
    return '<h5>humidity'+'➤ 20.7792 </h5> <i>(CTRL+C to copy to clipboard)</i>'
  }
    })
    .render()
    $('#lineChart').append('<h6 style="color:pink"> Avg. Temperature :'+ temp+'</h6>');
    $('#lineChart').append('<h6> Avg. Carbon dioxide (PPM) :'+ cd+'</h6>');
    $('#lineChart').append('<h6 style="color:green"> Avg. Humidity :'+ hud+'</h6>');
    var suggestion = 'Note: \n';
    if(temp > 18  ) suggestion += 'The average temp is higher than 18 °C, please dress properly \n'
    if(temp < 10 ) suggestion += 'The average temp is lower than 10 °C, please dress warm \n'
    if(  cd > 350  ) suggestion += 'The CO2 concantration is higher than avg. The room is crowded. \n'
    if (hud < 30 )  suggestion += 'The room is really dry \n'
    if (hud > 50 )  suggestion += 'The humidity level is higher than usual\n'
    $('#lineChart').append('<h6 style="color:red">'+suggestion+'</h6>');


}
// .defer(d3.json, 'http://smartspaces.r1.kth.se:8082/output/J3Wgj9qegGFX4r9KlxxGfaeMXQB.json?limit=9000&now-1day')

var showCir= function(url){
  circosLine = new Circos({
   container: '#lineChart',
   width: width,
   height: width
 })
d3.queue()
  .defer(d3.json, 'format.json')
  .defer(d3.json, url)
  .defer(d3.json, url)
  .defer(d3.json, url)
  .await(drawCircos)




}
