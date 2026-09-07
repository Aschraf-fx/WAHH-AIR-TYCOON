const A=require('node:assert/strict'),T=require('../app/src/main/assets/engine.js'),S=require('../app/src/main/assets/service.js');
function game(d='easy',cash=1000){let s=T.fresh();S.setup(s,{name:'Zara',difficulty:d,cash,mode:'solo',partner:'Ali',percent:50});s.open=true;s.speed=1;S.tick(s,.1);return s}
function drink(s,o){S.select(s,o.id);S.container(s,o.container);S.flavour(s,o.flavour);for(let[k,n]of Object.entries(S.targets(o))){let rate={powder:12,milk:16,water:110,ice:1}[k];for(let i=0;s.counter.cup[k]<n-.01&&i<1000;i++)S.pour(s,k,Math.min(.1,(n-s.counter.cup[k])/rate))}S.mix(s);S.lid(s)}
let s=game();A.equal(s.cash,1000);A.equal(game('normal',1000).cash,500);A.equal(game('hard',1000).cash,300);A.throws(()=>game('easy',1001));A.throws(()=>S.setup(s,{difficulty:'easy',cash:100}));
let o=S.order(s),cash=s.cash,stock={...s.stock};drink(s,o);let cost=s.counter.cup.cost,result=S.serve(s,o.id);A.equal(s.sold,1);A.equal(s.cash,T.round(cash+result.paid));A.equal(s.daily.cogs,T.round(cost));A.equal(s.stock.cups,stock.cups-1);A.equal(s.stock.lids,stock.lids-1);A.throws(()=>S.serve(s,o.id));
s=game();drink(s,S.order(s));S.discard(s);A.equal(s.daily.waste,1);A.ok(s.daily.cogs>0);let c=s.daily.cogs;S.discard(s);A.equal(c,s.daily.cogs);
s=game('hard');for(let i=0;i<60;i++)S.tick(s,1);A.ok(s.daily.lost>0);s=game();let first=S.order(s);for(let i=0;i<150;i++)S.tick(s,1);A.ok(s.counter.queue.some(o=>o.id===first.id));let patience=first.patience;s.speed=0;S.tick(s,2);A.equal(first.patience,patience);
s=game();s.profit=100;s.reinvest=0;T.distribute(s);A.equal(s.payouts[0].owner,'Zara');A.equal(s.payouts[0].ownerAmount,100);A.equal(s.payouts[0].partnerAmount,0);A.throws(()=>T.distribute(s));S.ownership(s,'split','Ali',60);s.profit=200;T.distribute(s);A.equal(s.payouts[0].ownerAmount,60);A.equal(s.payouts[0].partnerAmount,40);A.equal(s.payouts[1].ownerAmount,100);s.name='New';A.equal(s.payouts[1].owner,'Zara');
s=game();s.level=2;s.counter.queue=[];s.counter.selected=null;s.counter.arrival=0;s.nextId=4;S.tick(s,.1);o=S.order(s);A.ok(o.rider);cash=s.cash;for(let i=0;i<3;i++){drink(s,o);S.serve(s,o.id)}A.equal(s.sold,3);A.equal(s.daily.expenses,3);A.equal(s.cash,T.round(cash+o.price*3-3));A.equal(T.validate(JSON.parse(JSON.stringify(s))).counter.served,3);
console.log('PASS difficulty locks, capital bounds, manual cost/stock, waste, customer patience, solo/split payouts, immutable owner history, rider commission and save roundtrip');

const assert=require('node:assert/strict');
// Ready shelf operations preserve current preparation, accounting and XP.
{
let s=Tycoon.fresh();Service.ensure(s);s.open=true;s.speed=1;Service.tick(s,.01);let o=Service.order(s);let recipe=Service.targets(o);
s.counter.cup={container:o.container,flavour:o.flavour,...recipe,cost:1.23,mixed:true,lid:true};Service.storeDrink(s);let id=s.counter.shelf[0].id;
let current={container:'cups',flavour:null,powder:0,milk:0,water:0,ice:0,cost:.25,mixed:false,lid:false};s.counter.cup=current;let stock=JSON.stringify(s.stock);Service.serveStored(s,id,o.id);assert.equal(s.counter.cup,current);assert.equal(s.counter.xp,50);assert.equal(s.daily.cogs,1.23);assert.equal(JSON.stringify(s.stock),stock);assert.throws(()=>Service.serveStored(s,id,o.id));
s.counter.cup={container:'cups',flavour:s.unlocked[0],...recipe,cost:2,mixed:true,lid:true};Service.storeDrink(s);let d=s.counter.shelf[0];s.speed=0;Service.tick(s,2);assert.equal(d.age,0);s.speed=1;d.age=Service.freshness(s)-1;Service.tick(s,2);assert.equal(s.counter.shelf.length,0);assert.equal(s.daily.cogs,3.23);Service.tick(s,2);assert.equal(s.daily.cogs,3.23);s.counter.xp=600;assert.equal(Service.progress(s).level,4);assert.equal(Service.slots(s),5);Tycoon.validate(JSON.parse(JSON.stringify(s)));
console.log('PASS shelf save, serve once, working cup preservation, costs, pause, expiry once and XP unlocks');
}
