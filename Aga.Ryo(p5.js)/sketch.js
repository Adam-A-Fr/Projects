//variables menu
let mode="menu"; //mode de jeu initiale
let button,buttonA,buttonB; //boutons du menu
let colorPicker,colorPicker2; 

//variables jeu
let winPlayer1=false,winPlayer2=false; //booléennes victoire joueur
let windowX=1280,windowY=620; //taille de l'écran en x,y

//variables éléments du jeu
let x=200,x2=400,y=200,y2=200; //coordonnées initiales joueur
let speed1=3,speed2=3; //vitesse initiale joueur
let color1="#e65555",color2="#7cafde"; //couleur initiale joueur
let name1="player1",name2="player2"; //nom auto joueur
let diamBall1=100,diamBall2=80,diamFood=10,diamVirus=25,diamBonus=80,diamMalus=80; //diamètre éléments du jeu

//tableaux éléments du jeux
let foodX=[],foodY=[],foodColor=[]; //coordonnées + couleur de la nourriture 
let virusX=[],virusY=[],virusColor=[]; //coordonnées + couleur des virus 
let bonusX=[],bonusY=[],bonusColor=[]; //coordonnées + couleur des bonus 
let malusX=[],malusY=[],malusColor=[]; //coordonnées + couleur des malus 


//génère les images
function preload() {
  logo=loadImage('agaRyo.png');
}

//génère nourriture aléatoire 
function foodGenerate(number){
  for (let i=0 ; i< number ; i++){
    foodX.push(random(windowX)); //fonction push stock coordonnée x aléatoire 
    foodY.push(random(windowY));//stock coordonnée y aléatoire 
    foodColor.push(color(random(255),random(255),random(255))); //stock couleur rgb aléatoire
  }
}

//génère bonus aléatoire 
function bonusGenerate(number){
  for (let i=0 ; i< number ; i++){
    bonusX.push(random(windowX)); //stock coordonnée x aléatoire 
    bonusY.push(random(windowY)); //stock coordonnée y aléatoire 
    bonusColor.push(color('#ffcd29')); //stock couleur doré
}
}

//génère malus aléatoire 
function malusGenerate(number){
  for (let i=0 ; i< number ; i++){
    malusX.push(random(windowX)); //stock coordonnée x aléatoire 
    malusY.push(random(windowY)); //stock coordonnée y aléatoire 
    malusColor.push(color('#868992')); //stock couleur doré
}
}

//génère virus aléatoire 
function virusGenerate(number){
  for (let i=0 ; i< number ; i++){
    virusX.push(random(windowX)); //stock coordonnée x aléatoire
    virusY.push(random(windowY)); //stock coordonnée y aléatoire
    virusColor.push(color(0,255,0)); //stock couleur vert
  }
}

//dessine ou efface le(s) bonus + effet sur joueur
function bonusShow(){
  for (let i=bonusX.length-1;i>=0;i--){
    if (eat(bonusX[i],bonusY[i],x,y,diamBall1)){ //contact bonus et joueur1 ?
      bonusX.splice(i,1); //supprime coordonnée x du tableau
      bonusY.splice(i,1); //supprime coordonnée y du tableau
      bonusColor.splice(i,1); //supprime couleur du tableau
      color1='#ffcd29'; //le joueur1 prend la couleur du bonus (doré)
      speed1*=1.5; //la vitesse du joueur 1 augmente
    }
    else if (eat(bonusX[i],bonusY[i],x2,y2,diamBall2)){ //contact bonus et joueur2 ?
      bonusX.splice(i,1); //supprime coordonnée x du tableau
      bonusY.splice(i,1); //supprime coordonnée y du tableau
      bonusColor.splice(i,1); //supprime couleur du tableau
      color2='#ffcd29'; //le joueur2 prend la couleur du bonus (doré)
      speed2*=1.5; //la vitesse du joueur 2 augmente
    }
    else if (foodX.length < 220) { //s'il n'y plus que 150 coordonnées dans le tableau
      textSize(20); //taille du texte
      fill(255,0,0); //couleur rouge du texte
      text('BONUS DE VITESSE APPARU',windowX/2,windowY/2); //centre le texte "bonus vitesse"
      fill(bonusColor[i]); //rempli de la couleur du tableau
      ellipse(bonusX[i],bonusY[i],diamBonus); //fait une ellipse avec les tableaux du bonus
    }
  }
}

//dessine ou efface le(s) bonus + effet sur joueur
function malusShow(){
  for (let i=malusX.length-1;i>=0;i--){
    if (eat(malusX[i],malusY[i],x,y,diamBall1)){ //contact bonus et joueur1 ?
      malusX.splice(i,1); //supprime coordonnée x du tableau
      malusY.splice(i,1); //supprime coordonnée y du tableau
      malusColor.splice(i,1); //supprime couleur du tableau
      color1='#868992'; //le joueur1 prend la couleur du bonus (doré)
      diamBall1*=2; //la taille du joueur 1 augmente
    }
    else if (eat(malusX[i],malusY[i],x2,y2,diamBall2)){ //contact bonus et joueur2 ?
      malusX.splice(i,1); //supprime coordonnée x du tableau
      malusY.splice(i,1); //supprime coordonnée y du tableau
      malusColor.splice(i,1); //supprime couleur du tableau
      color2='#868992'; //le joueur2 prend la couleur du bonus (doré)
      diamBall2*=2; //la taille du joueur 2 augmente
    }
    else if (foodX.length < 150) { //s'il n'y plus que 150 coordonnées dans le tableau
      textSize(20); //taille du texte
      fill(255,0,0); //couleur rouge du texte
      text('BONUS DE TAILLE APPARU',windowX/2,windowY/2.5); //centre le texte "bonus vitesse"
      fill(malusColor[i]); //rempli de la couleur du tableau
      ellipse(malusX[i],malusY[i],diamBonus); //fait une ellipse avec les tableaux du bonus
    }
  }
}

//dessine ou efface la nourriture + effet sur joueur
function foodShow(){
  for (let i=foodX.length-1;i>=0;i--){
    if (eat(foodX[i],foodY[i],x,y,diamBall1)){ //contact nourriture et joueur1 ?
      foodX.splice(i,1); //supprime coordonnée x du tableau
      foodY.splice(i,1); //supprime coordonnée y du tableau
      foodColor.splice(i,1); //supprime couleur du tableau
      diamBall1 += diamFood*0.1; //diamètre joueur1 augmente selon nourriture
      
      if (speed1>=2) { //vitesse minimale
      speed1 *= 0.99; //vitesse joueur1 diminue
      }
    }
    else if (eat(foodX[i],foodY[i],x2,y2,diamBall2)){ //contact nourriture et joueur2 ?
      foodX.splice(i,1); //supprime coordonnée x du tableau
      foodY.splice(i,1); //supprime coordonnée y du tableau
      foodColor.splice(i,1); //supprime couleur du tableau
      diamBall2 += diamFood*0.1; //diamètre joueur2 augmente selon nourriture
      
      if (speed2>=2) { //vitesse minimale
      speed2 *= 0.99; //vitesse joueur2 diminue
      }
    }
    else {
      fill(foodColor[i]); //rempli de la couleur aléatoire du tableau
      ellipse(foodX[i],foodY[i],diamFood); //fait une ellipse avec les tableaux de la nourriture
    }
  }
}

//dessine ou efface le virus + effet sur joueur
function virusShow(){
  for (let i=virusX.length-1;i>=0;i--){
    if (eat(virusX[i],virusY[i],x,y,diamBall1) && diamBall1>diamVirus){ //contact virus et joueur1 ?
      virusX.splice(i,1); //supprime coordonnée x du tableau
      virusY.splice(i,1);  //supprime coordonnée y du tableau
      virusColor.splice(i,1); //supprime couleur du tableau
      diamBall1 = diamBall1/1.5; //diamètre joueur1 diminue
      speed1/=0.9; //vitesse joueur1 augmente
    }
    else if (eat(virusX[i],virusY[i],x2,y2,diamBall2)&& diamBall2>diamVirus){ //contact virus et joueur2 ?
      virusX.splice(i,1); //supprime coordonnée x du tableau
      virusY.splice(i,1); //supprime coordonnée y du tableau
      virusColor.splice(i,1); //supprime couleur du tableau
      diamBall2 = diamBall2/1.5; //diamètre joueur2 diminue
      speed2/=0.9; //vitesse joueur2 augmente
    }
    else {
      fill(virusColor[i]); //rempli de la couleur aléatoire du tableau
      ellipse(virusX[i],virusY[i],diamVirus); //fait une ellipse avec les tableaux du virus
    }
  }
}

//affiche joueur
function playerShow(diamBall,name,xA,yA,color) { 
  fill(color); //rempli l'ellipse de couleur 
  ellipse(xA,yA,diamBall,diamBall); //ellipse 
  fill(0); //texte de couleur noir
  textAlign(CENTER,CENTER); //centre le texte dans l'ellipse
  textSize(diamBall/2.5); //taille du texte
  text(name,xA,yA); //texte pseudo du joueur
}

//reset les tableaux de la nourriture
function foodReset(number){
  for (let i=foodX.length-1;i>=0;i--){
      foodX.splice(i,1); //supprime coordonnées x du tableau
      foodY.splice(i,1); //supprime coordonnées y du tableau
      foodColor.splice(i,1); //supprime couleur du tableau
  }
}

//reset les tableaux des bonus
function bonusReset(number){
  for (let i=bonusX.length-1;i>=0;i--){
      bonusX.splice(i,1); //supprime coordonnées x du tableau
      bonusY.splice(i,1); //supprime coordonnées y du tableau
      bonusColor.splice(i,1); //supprime couleur du tableau
  }
}

//reset les tableaux des malus
function malusReset(number){
  for (let i=malusX.length-1;i>=0;i--){
      malusX.splice(i,1); //supprime coordonnées x du tableau
      malusY.splice(i,1); //supprime coordonnées y du tableau
      malusColor.splice(i,1); //supprime couleur du tableau
  }
}

//reset les tableaux des virus
function virusReset(number){
  for (let i=virusX.length-1;i>=0;i--){
      virusX.splice(i,1); //supprime coordonnées x du tableau
      virusY.splice(i,1); //supprime coordonnées y du tableau
      virusColor.splice(i,1); //supprime couleur du tableau
  }
}


//renvoie si il y a contact entre deux éléments du jeu
function eat(xA,yA,xB,yB,diamBall){
  if (dist(xB,yB,xA,yA) <= diamBall/2 + diamFood/2 ){  
    return true; //oui il y a contact
  }
  else {
    return false; //non il n'y a pas contact
  }
}

//détruit le joueur le plus petit quand contact
function kill(xA,yA,xB,yB) {
    if (dist(xB,yB,xA,yA) <= 0.5*(diamBall1/2 + diamBall2/2) ) {
          if (diamBall1 < diamBall2) {
              winPlayer2=true; //le joueur2 gagne
              mode="menu"; //revenir au menu
              reset(); //appel fonction qui reset les éléments du jeu
              createslider(); //appel fonction crée boutons, slider etc...
              
          }
         else if (diamBall1 > diamBall2) {
              winPlayer1=true; //le joueur1 gagne
              mode="menu"; //revenir au menu
              reset(); //appel fonction qui reset les éléments du jeu
              createslider(); //appel fonction crée boutons, slider etc...
          }
  }
}

//reset les éléments du jeu
function reset() {
  bonusReset(1); //appel fonction reset les éléments du bonus
  malusReset(1); //appel fonction reset les éléments du malus
  foodReset(300); //appel fonction reset les éléments de nourriture
  virusReset(10); //appel fonction reset les éléments du bonus
  x=200;x2=400;y=200;y2=200; //coordonnées initiales
  diamBall1=25;diamBall2=25; //diamètre initiale
}

//déplacement joueur
function move(player,diamPlayer,xA,yA,speed,touche1,touche2,touche3,touche4) {
  let moveX=0,moveY=0; //variable déplacements
  
  if (keyIsDown(touche1) && xA>diamPlayer/2) { //si touche1 pressée et joueur dans l'écran
   moveX=-speed; 
  }
  if (keyIsDown(touche2) && xA<windowX-diamPlayer/2) { //si touche2 pressée et joueur dans l'écran
   moveX=speed;
  }
  if (keyIsDown(touche3) && yA>diamPlayer/2) { //si touche2 pressée et joueur dans l'écran
   moveY=-speed;
  }
  if (keyIsDown(touche4) && yA<windowY-diamPlayer/2) { //si touche2 pressée et joueur dans l'écran
    moveY=speed;
  }
  if (player==1) {
    x+=moveX;
    y+=moveY;    
  }
  else if (player==2) {
    x2+=moveX;
    y2+=moveY;    
  }
}

//cache les éléments du menu quand jeu lancé
function launchGame() {
  winPlayer1=false;winPlayer2=false;
  if (name1 == 'quentin') {
    color1="#fc03b6";
  }
  else color1=colorPicker.color();
  color2=colorPicker2.color();
  
  malusGenerate(1);
  bonusGenerate(1);
  foodGenerate(300);
  virusGenerate(10);
  slider.hide();
  slider2.hide();
  button.hide();
  buttonA.hide();
  buttonB.hide();
  colorPicker.hide();
  colorPicker2.hide();
  input.hide();
  input2.hide();
  mode="jeu";
  noStroke();
}

//éléments initiale (=menu)
function setup() {
  createslider(); //boutons, slider etc...
  createCanvas(windowX, windowY); //créer un écran de taille choisie
}

//fonction en boucle
function draw() {
  background(255); //fond blanc
  
  if (mode==="menu"){ //si le mode est en menu
  resizecanvas(); //appel fonction change écran selon sliders du menu
  Menu(); //appel fonction menu
  image(logo,50,50); //image du logo
  }
  
  
 else if (mode==="jeu"){ //si le mode est en jeu
  bonusShow(); //appel fonction affichage bonus
  malusShow(); //appel fonction affichage bonus
  foodShow(); //appel fonction affichage nourriture
  virusShow(); //appel fonction affichage virus
  kill(x,y,x2,y2); //appel fonction tue plus petit joueur

  move(1,diamBall1,x,y,speed1,LEFT_ARROW,RIGHT_ARROW,UP_ARROW,DOWN_ARROW); //déplacements joueur1
  move(2,diamBall2,x2,y2,speed2,81,68,90,83); //déplacements joueur2
  
  playerShow(diamBall1,name1,x,y,color1); //appel fonction affichage joueur1
  playerShow(diamBall2,name2,x2,y2,color2); //appel fonction affichage joueur2
 }
}


// MENU FONCTIONS

//crée boutons, sliders...
function createslider() {
  slider = createSlider(800, 1500, 900); //crée un slider
  slider2 = createSlider(600, 1200, 830); //crée un slider
  slider.position(40, 20); //position du premier slider
  slider2.position(40, 50); //position du second slider
  slider.style('width', '200px'); //taille premier slider
  slider2.style('width', '200px'); //taille second slider
  
  colorPicker = createColorPicker('#ff0000'); //crée un colorpicker
  colorPicker.position(290,320); //position premier colorpicker
  colorPicker2 = createColorPicker('#0000ff'); //crée un colorpicker
  colorPicker2.position(290,420); //position second colorpicker
  button = createButton('Play'); //crée un bouton play
  button.class('play'); //classe pour css du bouton play
  button.position(400, 500); //position du bouton play
  button.mousePressed(launchGame); //lance jeu quand bouton préssé 
  
  
  //choisir le nom du joueur 1
  input = createInput(); 
  input.position(350, 322);

  buttonA = createButton('pseudo 1');
  buttonA.class('name');
  buttonA.position(input.x + input.width, 322);
  buttonA.mousePressed(name);
  textAlign(CENTER);
  
  //choisir le nom du joueur 2
  input2 = createInput();
  input2.position(350, 422);

  buttonB = createButton('pseudo 2');
  buttonB.class('name');
  buttonB.position(input2.x + input2.width, 422);
  buttonB.mousePressed(name);
  textAlign(CENTER);
}

function resizecanvas() {
  windowX = slider.value();
  windowY = slider2.value();
  resizeCanvas(windowX, windowY);
  background('#222224');
}

function Menu() {
  fill("#3e3f42");
  stroke(255);
  strokeWeight(2);
  rect(30, 20, windowX-100, windowY-100, 7);
  if (winPlayer1) {
    textAlign(CENTER,CENTER);
    textSize(20);
    text(name1+' a gagné !',450,600);
  }
  else if (winPlayer2) {
    textAlign(CENTER,CENTER);
    textSize(20);
    text(name2+' a gagné !',450,600); 
  }
}

function name() {
  name1 = input.value();
  name2 = input2.value();
}
