#include "libTableauNoir.h"
#include <stdio.h>
#include <stdlib.h>

/***********************/
/* définition de TYPES */
/***********************/

/* coordonnées dans le plan */
typedef struct {double x; double y;} coord_t;

/* joueur*/
typedef struct {coord_t a; coord_t b;} player_t;

/* la balle avec une position et une vitesse*/
typedef struct {coord_t p; coord_t v;} balle_t;

/* la première brique servant de référence*/
typedef struct {coord_t c; coord_t d;} bloc_t;

/* modèle de base*/
typedef struct {
    player_t player;
    balle_t balle;
    bloc_t blocA;  
} modele_t;

/* structure d'images*/
typedef struct {
    
    Image vie;
    Image acc1;
    Image acc4;
    Image tuto;
    Image skip;
    Image tips;
    Image win;
    Image start;
    Image end;
    Image num0;
    Image num1;
    Image num2;
    Image num3;
    Image num4;
    Image num5;
    Image num6;
    Image num7;
    Image num8;
    Image num9;

} images_t;

/* structure de sons*/
typedef struct {
    
    Son menu;
    Son win;
    Son loose;
    Son brick;
    Son hit;
 
} son_t;
    
/* structure sontenant toutes les informations relatives aux briques*/
typedef struct {
    bloc_t bloc[54]; 
    int contact[54];
    int test[54];
} brique_t;    

/* stylos utilisés pour tracer les objets */
typedef struct {
    tStylo player;
    tStylo balle;
    tStylo bloc;
} stylos_t;

/* Donnees global à tous le jeu*/
typedef struct {
    modele_t modele;
    stylos_t stylos;
    int fini;
    int vie;
    int score;
    int start;
    int first;
    int clavier;
    int souris;
    int screen;
    int gauche;
    int droite;
    int sound;
    int win;
    int fisrt_win;
    int fisrt_loose;
} donnees_t;

/********************************/
/* déclaration des fonctions    */
/********************************/
modele_t init_modele();
stylos_t init_stylos();
brique_t init_brique(donnees_t);
donnees_t initialiser_donnees();
images_t init_image();
son_t init_son();

int fini(donnees_t);
int aleatoire();

void effacer(donnees_t);
void init_vie_et_score(images_t);
void afficher();
void mettre_a_jour(donnees_t *,brique_t,son_t);
void lib_images(images_t p);
void afficherBloc(brique_t*);
void Bloc(donnees_t*,brique_t*,images_t,son_t);
void score(donnees_t *,images_t);
void score_unite(donnees_t *,images_t);
void zoom_score(images_t *);
void win(donnees_t *,images_t,son_t);
void contact(donnees_t*,brique_t*,int);
void start(donnees_t *,images_t);
void end(donnees_t *,images_t,son_t);
void menu(donnees_t *, images_t,son_t);
void menu1(donnees_t *, images_t,son_t);
void menu2(donnees_t *, images_t,son_t);
void menu3(donnees_t *, images_t,son_t);
void jeu();


/***********************/
/* INITIALISATION */
/***********************/
/* fonction permetant d'obtenir une couleur aléatoire */
int aleatoire()
{
    int n;
    n= 0+rand()%(255+1-0);
    return n;
    
} 
/* initialisation des données */
donnees_t initialiser_donnees()
{
    donnees_t donnees = { .fini=0 };
    donnees.modele = init_modele();
    donnees.stylos = init_stylos();
    donnees.vie= 3;
    donnees.score=0;
    donnees.start=0;
    donnees.first=0;
    donnees.clavier=0;
    donnees.souris=0;
    donnees.screen=0;
    donnees.gauche=0;
    donnees.droite=0;
    donnees.sound=0;
    donnees.win=0; 
    donnees.fisrt_win=0;
    donnees.fisrt_loose=0;
    return donnees ;
}
/* initialisation du modèle */
modele_t init_modele()
{
    modele_t m = {balle: {p: {0, -215}, v: {0, (5)}},
		    player: {a: {-50, -225}, b: {50, -240}},
		  blocA: {c: {-275, 220}, d: {-229.5, 195}}};
		  
     return m;
}

/* initialisation des données "brique"*/
brique_t init_brique(donnees_t donnees)
{
    brique_t b;
    int i,j;  
    for(j=0;j<6;j++)
    {	    
	for(i=0;i<9;i++)
	{
	    donnees.modele.blocA.c.x=donnees.modele.blocA.d.x+5;
	    donnees.modele.blocA.d.x=donnees.modele.blocA.c.x+45.5;
	    b.bloc[i+j*9].c.x=donnees.modele.blocA.c.x;
	    b.bloc[i+j*9].c.y=donnees.modele.blocA.c.y;
	    b.bloc[i+j*9].d.x=donnees.modele.blocA.d.x;
	    b.bloc[i+j*9].d.y=donnees.modele.blocA.d.y;
	    b.contact[i+j*9]=0;
	    b.test[i+j*9]=0;
	}
    	donnees.modele.blocA.c.y=donnees.modele.blocA.d.y-5;
	donnees.modele.blocA.d.y=donnees.modele.blocA.c.y-25;
	donnees.modele.blocA.c.x=-275;
	donnees.modele.blocA.d.x=-229.5;	 
    }
    return b;
} 

/* initialisation des stylos */
stylos_t init_stylos()
{
    stylos_t stylos;
    choisirTypeStylo(1,255,  255,  255); stylos.player = stockerStylo();
    choisirTypeStylo(7,255,  0,  0); stylos.balle = stockerStylo();
    choisirTypeStylo(1,0,0,0); stylos.bloc = stockerStylo();
    return stylos;
}

/* initialisation des images */
images_t init_image()
{
    images_t p;
    
    p.vie = chargerImage("nombre/coeur.jpg");
    p.acc1 = chargerImage("nombre/menu1.PNG");
    p.acc4 = chargerImage("nombre/menu4.PNG");
    p.tuto = chargerImage("nombre/tuto.PNG");
    p.skip = chargerImage("nombre/skip.jpg");
    p.tips = chargerImage("nombre/tips.PNG");
    p.win = chargerImage("nombre/win.jpg");
    p.start = chargerImage("nombre/start.PNG");
    p.end = chargerImage("nombre/end.jpg");
    p.num0 = chargerImage("nombre/num0.PNG");
    p.num1 = chargerImage("nombre/num1.PNG");
    p.num2 = chargerImage("nombre/num2.PNG");
    p.num3 = chargerImage("nombre/num3.PNG");
    p.num4 = chargerImage("nombre/num4.PNG");
    p.num5 = chargerImage("nombre/num5.PNG");
    p.num6 = chargerImage("nombre/num6.PNG");
    p.num7 = chargerImage("nombre/num7.PNG");
    p.num8 = chargerImage("nombre/num8.PNG");
    p.num9 = chargerImage("nombre/num9.PNG");
    
    return p;
} 

/* initialisation des sons */
son_t init_son()
{
    son_t p;
    
    p.menu= chargerAudio("son/menu.mp3");
    p.win= chargerAudio("son/win.mp3");
    p.loose=chargerAudio("son/loose.mp3");
    p.brick= chargerAudio("son/brick.mp3");
    p.hit= chargerAudio("son/hit.mp3");
    
    return p;
} 
   
/* fontion permetant de creer la condition fermant le jeu */
int fini(donnees_t donnees)
{
    return donnees.fini;
}

/* fontion permetant d'initialiser l'affichage des briques */
void afficherBloc(brique_t * b) 
{
    int i,j;  
    for(j=0;j<6;j++)
    {	    
	for(i=0;i<9;i++)
	{
	    choisirCouleurPinceau(aleatoire(),aleatoire(),aleatoire());
	    tracerRectangle(b->bloc[i+(j*9)].c.x, b->bloc[i+(j*9)].c.y, b->bloc[i+(j*9)].d.x, b->bloc[i+(j*9)].d.y);
	}
    }
} 

/* fontion permetant d'initialiser le score et la vie */
void init_vie_et_score(images_t p)
{
    int i;
    p.vie= rotozoomImage(p.vie,0, 0.2, 0.2);
    p.num0= rotozoomImage(p.num0,0, 0.15, 0.15);
    effacerRectangle(-294,300,-270,255);
    afficherImage(p.num0, -294, 290);
    for(i=0;i<3;i++)
    {
	afficherImage(p.vie, 150+(i*50), 290);
    }	
    libererImage(p.vie);
} 

/* fontion permetant d'initialiser le menu interactif */
void menu(donnees_t * donnees, images_t p,son_t s)
{	
    choisirCouleurPinceau(255,255,255);	    
	while (donnees->screen==0)
	{
	    EtatSourisClavier esc = lireEtatSourisClavier(); 
	    afficherImage(p.acc1, -300,200);
	    effacerRectangle(150,-105,300,-300);  
    
	    if (esc.touchesClavier[80]==1) {
		donnees->gauche=1;
		donnees->droite=0;
	    }
	    if (esc.touchesClavier[79]==1) {
		donnees->droite=1;
		donnees->gauche=0;
	    }	
	    if(donnees->gauche==1)
	    {  
		tracerRectangle(-208,-2,-158,-5);
		if (esc.touchesClavier[40]==1) 
		{
		    jouerSon(s.menu,0);
		    choisirCouleurPinceau(255,0,0);
		    tracerRectangle(-208,-2,-158,-5);
		    donnees->clavier=1;
		    printf("Le clavier est de %d \n",donnees->clavier);
		    donnees->screen=1;
		}
	    }	
	    if(donnees->droite==1)
	    {  
		tracerRectangle(140,-2,175,-5);
		if (esc.touchesClavier[40]==1) 
		{
		    jouerSon(s.menu,0);
		    choisirCouleurPinceau(255,0,0);
		    tracerRectangle(140,-2,175,-5);
		    donnees->souris=1;
		    donnees->screen=1;
		}
	    }	
	    tamponner();
	}
	attendreNms(600);
	effacerTableau();
	donnees->droite=0;
	donnees->gauche=0;
}	
/* fontion permetant d'initialiser le menu interactif */	
void menu1(donnees_t * donnees, images_t p,son_t s)
{
    choisirCouleurPinceau(255,255,255);	

    while (donnees->screen==1)
	{
	    EtatSourisClavier esc = lireEtatSourisClavier(); 
	    choisirCouleurPinceau(255,255,255);
	    afficherImage(p.acc4, -300,200);
	    effacerRectangle(150,-130,300,-300); 
	    
	    if (esc.touchesClavier[80]==1) {
		donnees->gauche=1;
		donnees->droite=0;
	    }
	    if (esc.touchesClavier[79]==1) {
		donnees->droite=1;
		donnees->gauche=0;
	    }	
	    if(donnees->gauche==1)
	    {  
		tracerRectangle(-219,3,-185,0);
		if (esc.touchesClavier[40]==1) 
		{
		    if(donnees->sound==1){jouerSon(s.menu,0);}
		    choisirCouleurPinceau(255,0,0);
		    tracerRectangle(-219,3,-185,0);
		    donnees->sound=1;
		    donnees->screen=2;
		}
	    }	
	    if(donnees->droite==1)
	    {  
		tracerRectangle(150,3,180,0);
		if (esc.touchesClavier[40]==1) 
		{
		    if(donnees->sound==1){jouerSon(s.menu,0);}
		    choisirCouleurPinceau(255,0,0);
		    tracerRectangle(150,3,180,0);
		    donnees->sound=0;
		    donnees->screen=2;
		}
	    }	
	    tamponner();
	}
}
/* fontion permetant d'initialiser le menu interactif */
void menu2(donnees_t * donnees, images_t p,son_t s)
{
    attendreNms(600);
    effacerTableau();
    choisirCouleurPinceau(255,255,255);	

    p.skip= rotozoomImage(p.skip,0, 0.4, 0.4);
    while (donnees->screen==2)
	{
	    EtatSourisClavier esc = lireEtatSourisClavier(); 
	    afficherImage(p.tuto, -300,300);
	    choisirCouleurPinceau(255,255,255);
	    tracerRectangle(147,-113,264,-147);
	    afficherImage(p.skip, 150,-116);
	    
	    if  ((esc.sourisX>=147) && (esc.sourisX<=264) &&
	     (esc.sourisY>=-147) && (esc.sourisY<=-113) 
	    )
	    {
		choisirCouleurPinceau(255,0,0);
		tracerRectangle(147,-113,264,-147);
		afficherImage(p.skip, 150,-116);
		if (esc.sourisBoutonGauche) 
		{
		    if(donnees->sound==1){jouerSon(s.menu,0);}
		    donnees->screen = 3;
		}
	    }
	    tamponner();
	}
	effacerTableau();
}	
/* fontion permetant d'initialiser le menu interactif */
void menu3(donnees_t * donnees, images_t p,son_t s)
{
    choisirCouleurPinceau(255,255,255);	
    p.skip= rotozoomImage(p.skip,0, 0.4, 0.4);
	while (donnees->screen==3)
	{
	    EtatSourisClavier esc = lireEtatSourisClavier();  
	    afficherImage(p.tips, -300,300);
	    choisirCouleurPinceau(255,255,255);
	    tracerRectangle(147,-147,264,-181);
	    afficherImage(p.skip, 150,-150);
	    
	    if  ((esc.sourisX>=147) && (esc.sourisX<=264) &&
	     (esc.sourisY>=-181) && (esc.sourisY<=-147) 
	    )
	    {
		choisirCouleurPinceau(255,0,0);
		tracerRectangle(147,-147,264,-181);
		afficherImage(p.skip, 150,-150);
		if (esc.sourisBoutonGauche) 
		{
		    if(donnees->sound==1){jouerSon(s.menu,0);}
		    donnees->screen = 4;
		}
	    }
	    tamponner();
	}	
}	
/* fontion permetant d'initialiser le menu de victoire*/
void win(donnees_t * donnees,images_t p,son_t s)
{
    EtatSourisClavier esc = lireEtatSourisClavier();
    if(donnees->win==1)
    {
	if(donnees->fisrt_win==0)
	{
	if(donnees->sound==1){jouerSon(s.win,0);}
	donnees->fisrt_win=1;
	}
	effacerTableau();
	p.win= rotozoomImage(p.win,0, 0.5, 0.5);
	afficherImage(p.win, -115, 150);
	selectionnerStylo(donnees->stylos.player); 
    
	if  ((esc.sourisX>=-25) && (esc.sourisX<=47.5) &&
	     (esc.sourisY>=47.5) && (esc.sourisY<=65) 
	    )
	{
	    tracerSegment(-25,47.5,47.5,47.5);
	    if (esc.sourisBoutonGauche) 
	    {
		effacerRectangle(-115,150,200,-150);
		jeu();
	    }
	}
	
	if  ((esc.sourisX>=-25) && (esc.sourisX<=0) &&
	     (esc.sourisY>=2.5) && (esc.sourisY<=20) 
	    )
	{
	    tracerSegment(-25,2.5,0,2.5);
	    if (esc.sourisBoutonGauche) {
		donnees->fini = 1;
	    }
	}
	libererImage(p.win);
    }
} 
/* fontion permetant d'initialiser la page entre chaque "demande de re start"*/
void start(donnees_t * donnees,images_t p)
{
    if(donnees->start==0)
    {
	    p.start= rotozoomImage(p.start,0, 1.25, 1.25);
	    afficherImage(p.start, -120,-130);
	    libererImage(p.start);
    } 
} 
/* fontion permetant d'initialiser le menu de défaite*/
void end(donnees_t * donnees,images_t p, son_t s)
{
    EtatSourisClavier esc = lireEtatSourisClavier();  
	    
    if(donnees->vie==0)
    {
	if(donnees->fisrt_loose==0)
	{
	if(donnees->sound==1){jouerSon(s.loose,0);}
	donnees->fisrt_loose=1;
	}
	effacerTableau();
	p.end= rotozoomImage(p.end,0, 0.5, 0.5);
	afficherImage(p.end, -115, 150);
	selectionnerStylo(donnees->stylos.player); 
    
	if  ((esc.sourisX>=-25) && (esc.sourisX<=47.5) &&
	     (esc.sourisY>=47.5) && (esc.sourisY<=65) 
	    )
	{
	    tracerSegment(-25,47.5,47.5,47.5);
	    if (esc.sourisBoutonGauche) 
	    {
		effacerRectangle(-115,150,200,-150);
		jeu();
	    }
	}
	
	if  ((esc.sourisX>=-25) && (esc.sourisX<=0) &&
	     (esc.sourisY>=2.5) && (esc.sourisY<=20) 
	    )
	{
	    tracerSegment(-25,2.5,0,2.5);
	    if (esc.sourisBoutonGauche) {
		donnees->fini = 1;
	    }
	}
	libererImage(p.end);
    }
    
}
/* fontion permetant d'initialiser le zoom des images du score*/
void zoom_score(images_t * p)
{
    p->num0= rotozoomImage(p->num0,0, 0.15, 0.15);
    p->num1= rotozoomImage(p->num1,0, 0.15, 0.15);
    p->num2= rotozoomImage(p->num2,0, 0.15, 0.15);
    p->num3= rotozoomImage(p->num3,0, 0.15, 0.15);
    p->num4= rotozoomImage(p->num4,0, 0.15, 0.15);
    p->num5= rotozoomImage(p->num5,0, 0.15, 0.15);
    p->num6= rotozoomImage(p->num6,0, 0.15, 0.15);
    p->num7= rotozoomImage(p->num7,0, 0.15, 0.15);
    p->num8= rotozoomImage(p->num8,0, 0.15, 0.15);
    p->num9= rotozoomImage(p->num9,0, 0.15, 0.15);
    
}	
/* fontion permetant d'afficher les unitées du score*/
void score_unite(donnees_t * donnees, images_t p)
{
    int nombre= donnees->score;
    int i;
    for(i=0;i<=5;i++)
    {
	if(nombre==(i*10)+0)
	{
	    effacerRectangle(-265,300,-235,255);
	    afficherImage(p.num0, -265, 290);
	} 
	else if(nombre==(i*10)+1)
	{
	    effacerRectangle(-265,300,-235,255);
	    afficherImage(p.num1, -265, 290);
	} 
	
	else if(nombre==(i*10)+2)
	{
	    effacerRectangle(-265,300,-235,255);
	    afficherImage(p.num2, -265, 290);
	} 
	else if(nombre==(i*10)+3)
	{
	    effacerRectangle(-265,300,-235,255);
	    afficherImage(p.num3, -265, 290);
	} 
	else if(nombre==(i*10)+4)
	{
	    effacerRectangle(-265,300,-235,255);
	    afficherImage(p.num4, -265, 290);
	} 
	else if(nombre==(i*10)+5)
	{
	    effacerRectangle(-265,300,-235,255);
	    afficherImage(p.num5, -265, 290);
	} 
	else if(nombre==(i*10)+6)
	{
	    effacerRectangle(-265,300,-235,255);
	    afficherImage(p.num6, -265, 290);
	} 
	else if(nombre==(i*10)+7)
	{
	    effacerRectangle(-265,300,-235,255);
	    afficherImage(p.num7, -265, 290);
	} 
	else if(nombre==(i*10)+8)
	{
	    effacerRectangle(-265,300,-235,255);
	    afficherImage(p.num8, -265, 290);
	}  
	else if(nombre==(i*10)+9)
	{
	    effacerRectangle(-265,300,-235,255);
	    afficherImage(p.num9, -265, 290);
	}
    }
} 	

/* fontion permetant d'afficher l'entiereté du score*/
void score(donnees_t * donnees, images_t p)
{
    int nombre= donnees->score;
    zoom_score(&p);
    score_unite(donnees,p);
	
    if(nombre>=10)
    {
	effacerRectangle(-294,300,-270,255);
	afficherImage(p.num1, -294, 290);
    } 
    if(nombre>=20)
    {
	effacerRectangle(-294,300,-270,255);
	afficherImage(p.num2, -294, 290);
    } 
    
    if(nombre>=30)
    {
	effacerRectangle(-294,300,-270,255);
	afficherImage(p.num3, -294, 290);
    } 
    if(nombre>=40)
    {
	effacerRectangle(-294,300,-270,255);
	afficherImage(p.num4, -294, 290);
    } 
    if(nombre>=50)
    {
	effacerRectangle(-294,300,-270,255);
	afficherImage(p.num5, -294, 290);
    } 
}

/* fontion permetant de liberer les images*/
void lib_images(images_t p)
{
    libererImage(p.num0);
    libererImage(p.num1);
    libererImage(p.num2);
    libererImage(p.num3);
    libererImage(p.num4);
    libererImage(p.num5);
    libererImage(p.num6);
    libererImage(p.num7);
    libererImage(p.num8);
    libererImage(p.num9);
} 
    

/***********************/
/* MISE A JOUR */
/***********************/
/* fontion permetant de détecter un contact entre la balle et les briques*/
void contact(donnees_t * donnees, brique_t * b, int c)
{

    if  ((donnees->modele.balle.p.x+5>=b->bloc[c].c.x) && (donnees->modele.balle.p.x-5<=b->bloc[c].d.x) &&
	(donnees->modele.balle.p.y<=b->bloc[c].c.y) && (donnees->modele.balle.p.y>=b->bloc[c].d.y))		
	{
	    b->contact[c]=1;
	}
} 

/* fontion permetant de gérer toutes les intéractions relatives aux briques*/
void Bloc(donnees_t * donnees, brique_t* b, images_t p,son_t s)
{
    int i,j;
    int n,r;
    for(j=0;j<6;j++)
    {	    
	for(i=0;i<9;i++)
	{
	    contact(donnees,b,i+(j*9));
	    /* On vérifie si c'est la première fois que l'on touche la brique*/
	    if  ( (b->contact[i+(j*9)]==1) && ((b->test[i+(j*9)])==0) )	
	    {
		if(donnees->sound==1){jouerSon(s.brick,0);}
		effacerRectangle(b->bloc[i+(j*9)].c.x, b->bloc[i+(j*9)].c.y, b->bloc[i+(j*9)].d.x, b->bloc[i+(j*9)].d.y);
		
		/* Rebond avec Player en X*/      
		if  (	 (donnees->modele.balle.p.x+5>=b->bloc[i+(j*9)].c.x) && (donnees->modele.balle.p.x-5<=b->bloc[i+(j*9)].d.x) &&
			 (donnees->modele.balle.p.y<b->bloc[i+(j*9)].c.y) && (donnees->modele.balle.p.y>b->bloc[i+(j*9)].d.y))
		{
		    donnees->modele.balle.v.x=donnees->modele.balle.v.x*-1; 
		}		
		/* Rebond avec Player en Y*/
		else if ((((donnees->modele.balle.p.y-5)<=b->bloc[i+(j*9)].c.y) && ((donnees->modele.balle.p.y+5)>=b->bloc[i+(j*9)].d.y) )&& 
		(((donnees->modele.balle.p.x)>b->bloc[i+(j*9)].c.x) && ((donnees->modele.balle.p.x)<b->bloc[i+(j*9)].d.x) ) )
		{
		    donnees->modele.balle.v.y= (donnees->modele.balle.v.y)*-1;
		} 
		/* Rebond avec Player dans les angles défini aléatoirement("variante")*/
		else
		{
		    n= 0+rand()%(2+1-0);
		    for(r=0;r<n;r++)
		    {	
		    donnees->modele.balle.v.y= (donnees->modele.balle.v.y)*-1;
		    }
		    n= 0+rand()%(2+1-0);
		    for(r=0;r<n;r++)
		    {	
		    donnees->modele.balle.v.x=donnees->modele.balle.v.x*-1; 
		}
		} 
		b->test[i+(j*9)]=1;
		donnees->score=donnees->score+1;
		score(donnees,p);
		if(donnees->score==54)
		{ 
		    attendreNms(600);
		    donnees->win=1;
		}
		printf("Le score est de %d \n",donnees->score);
		
	    } 
	    
	}
    }
} 

/* effacer les objets mouvants */
void effacer(donnees_t donnees)
{
    effacerRectangle(donnees.modele.player.a.x, donnees.modele.player.a.y, donnees.modele.player.b.x, donnees.modele.player.b.y);
    effacerPoint(donnees.modele.balle.p.x, donnees.modele.balle.p.y);
} 

/* afficher les objets mouvants */
void afficher(donnees_t donnees)
{
    if(donnees.vie!=0)
    {
	choisirCouleurPinceau(255,255,255);
	selectionnerStylo(donnees.stylos.player); 
	tracerRectangle(donnees.modele.player.a.x, donnees.modele.player.a.y, donnees.modele.player.b.x, donnees.modele.player.b.y);
	choisirTypeStylo(5,132,46,27); 
	tracerSegment(-300,250,300,250);
	choisirCouleurPinceau(255,0,0);
	selectionnerStylo(donnees.stylos.balle); 
	tracerSegment(donnees.modele.player.a.x+50,donnees.modele.player.a.y, donnees.modele.player.b.x-50, donnees.modele.player.b.y);
	tracerPoint(donnees.modele.balle.p.x, donnees.modele.balle.p.y);
    }
}


/* mettre à jour l'état du programme */
void mettre_a_jour(donnees_t * donnees,brique_t b, son_t s)
{
    EtatSourisClavier esc = lireEtatSourisClavier();    
    /* Rebond dans la fenetre*/
    if((donnees->modele.balle.p.y+5)==250)
    {
	if(donnees->sound==1){jouerSon(s.hit,0);}
	donnees->modele.balle.v.y= (donnees->modele.balle.v.y)*-1;
	
    }
    
    if((donnees->modele.balle.p.x+5)==300)
    {
	if(donnees->sound==1){jouerSon(s.hit,0);}
	donnees->modele.balle.v.x= (donnees->modele.balle.v.x)*-1;
    }  
    if((donnees->modele.balle.p.x-5)==-300)
    {
	if(donnees->sound==1){jouerSon(s.hit,0);}
	donnees->modele.balle.v.x= (donnees->modele.balle.v.x)*-1;
    } 
    
    /* Rebond dans la fenetre et on gère la perte de vie*/
    if((donnees->modele.balle.p.y+5)<-300)
    {
      donnees->vie=donnees->vie-1;  
      printf("La vie est de %d \n",donnees->vie);
      if(donnees->vie==2){effacerRectangle(150,290,180,262);}
      if(donnees->vie==1){effacerRectangle(200,290,230,262);}
      if(donnees->vie==0){effacerRectangle(250,290,280,262);}
      donnees->modele.player.a.x=-50;
      donnees->modele.player.b.x=50;
      donnees->modele.balle.p.x=0;
      donnees->modele.balle.p.y= -215;
      donnees->start=0;
      donnees->modele.balle.v.x= 0;  
      donnees->modele.balle.v.y=5;
    }	 
    
    /* Rebond avec Player en Y*/
    if( (((donnees->modele.balle.p.y-5)==-225)) &&  (((donnees->modele.balle.p.x+5)>=donnees->modele.player.a.x) 
     && ((donnees->modele.balle.p.x-5)<=donnees->modele.player.b.x) ) )
    {
	if(donnees->modele.balle.p.x-5>=(donnees->modele.player.a.x+50))
	{
	    donnees->modele.balle.v.x=5;
	}
	else if (donnees->modele.balle.p.x+5<=(donnees->modele.player.a.x+50))
	{
	    donnees->modele.balle.v.x=-5;
	} 
	else 
	{
	    donnees->modele.balle.v.x=0;
	} 
	if(donnees->sound==1){jouerSon(s.hit,0);}
	donnees->modele.balle.v.y= (donnees->modele.balle.v.y)*-1;
    }	
    
    /* Rebond avec Player en X  */    
    
    if  ( ( ((donnees->modele.balle.p.x+5>=donnees->modele.player.a.x) && (donnees->modele.balle.p.x+5<=donnees->modele.player.a.x+5))
	||  ((donnees->modele.balle.p.x-5<=donnees->modele.player.b.x) && (donnees->modele.balle.p.x-5>=donnees->modele.player.b.x-5))  ) &&
	((donnees->modele.balle.p.y<=-225) && (donnees->modele.balle.p.y>=-240)) )
    {
	if(donnees->sound==1){jouerSon(s.hit,0); }
	donnees->modele.balle.v.x=donnees->modele.balle.v.x*-1;  
	donnees->modele.balle.v.y=donnees->modele.balle.v.y*-1; 
		   
    }
    
   /* On vérifie à chaque mise à jour si relance il doit y avoir, si l'utilisateur est près */ 
    if (esc.touchesClavier[44]==1) {
	donnees->start = 1;
    }
    
    /* On vérifie à chaque mise à jour si le jeu est bien en cours  */ 
    if(donnees->start==1)
    {
	effacerRectangle(-300,20,300,-185);	
	donnees->modele.balle.p.x= donnees->modele.balle.p.x+donnees->modele.balle.v.x;
	donnees->modele.balle.p.y= donnees->modele.balle.p.y+donnees->modele.balle.v.y;
	/* Déplacements souris*/

	    if(esc.sourisX+50<=300 && esc.sourisX-50>=-300 && donnees->souris==1)
	    {
		donnees->modele.player.a.x= esc.sourisX-50;
		donnees->modele.player.b.x= esc.sourisX+50;
	    }
	/* Déplacements clavier*/
	    if(donnees->modele.player.a.x<=200)
	    {
		if(esc.touchesClavier[79]==1 && donnees->clavier==1)
		{
		    donnees->modele.player.a.x= donnees->modele.player.a.x+7;
		    donnees->modele.player.b.x= donnees->modele.player.b.x+7;
		}
		
	    }
	    if(donnees->modele.player.a.x>=-300)
	    {
		if(esc.touchesClavier[80]==1 && donnees->clavier==1)
		{
		    donnees->modele.player.a.x= donnees->modele.player.a.x-7;
		    donnees->modele.player.b.x= donnees->modele.player.b.x-7;
		}	
	    }
    }
}

void jeu()
{
    /* initialisation général du jeu */ 
    donnees_t donnees = initialiser_donnees();
    brique_t b= init_brique(donnees);
    images_t p= init_image();
    son_t s= init_son();
    /* On vérifie à quelle "page" du jeu on se trouve */ 
    if(donnees.screen==0)
    {
	menu(&donnees,p,s);
	menu1(&donnees,p,s);
	menu2(&donnees,p,s);
	menu3(&donnees,p,s);
    }
    effacerTableau();
    /* On vérifie à quelle "page" du jeu on se trouve */ 
    if(donnees.screen==4)
    {
	/* On affiche les objets qu'on affcihe qu'une seule fois*/ 
	afficherBloc(&b);
	init_vie_et_score(p);
	/* On rentre dans la boucle d'animation*/ 
	while(!fini(donnees))
	{
		/* mettre à jour les donnees */
		if(donnees.score!=54)
		{
		    Bloc(&donnees,&b,p,s);
		    mettre_a_jour(&donnees,b,s);
		    start(&donnees,p);
		    end(&donnees,p,s);
		}	
		win(&donnees,p,s);
		/* créer l'image sur le buffer */
		if(donnees.score!=54)
		{
		    afficher(donnees);
		}
		/* afficher le buffer à l'écran */
		tamponner();	
		/* effacer le buffer */
		effacer(donnees);
		if (fermetureDemandee()) {donnees.fini = 1;}		
	}
	lib_images(p);
    }		
}

 
	      
int main() 
{
    /* On crée et initialise la fenetre graphique */ 
    creerTableau();	
    fixerModeBufferisation(1);
    fixerTaille(600,600);
    jeu();
    return 0;
}
