public class Player extends Element{
   int posX,posY;
   Level world;
   public Player(int x,int y,Level w){
      this.sprite = "assets/player.png";
      posX = x;
      posY =y ;
      world=w;
   }

   //On test pour chaque direction la bordure pour trouver une entrée
   public int entryLeft(){
      if(world.map[0][world.size/2] instanceof EmptySpace) return world.size/2;
      return -1;
   }
   public int entryUp(){
      if(world.map[world.size/2][0] instanceof EmptySpace) return world.size/2;
      return -1;
   }
   public int entryRight(){
      if(world.map[world.size-1][world.size/2] instanceof EmptySpace) return world.size/2;
      return -1;
   }
   public int entryDown(){
      if(world.map[world.size/2][world.size-1] instanceof EmptySpace) return world.size/2;
      return -1;
   }

   //Génération des sorties d'un Lvl
   public void exitGen(Level l,int x,int y){

      while(!world.exit.isEmpty()) world.exit.remove(0);

      for(int i=0;i< world.size;i++){
         if(world.map[i][world.size-1]instanceof EmptySpace) this.world.exit.add(new Exit(l,i, world.size-1,"down",x,y));
         if(world.map[i][0]instanceof EmptySpace) this.world.exit.add(new Exit(l,i, 0,"up",x,y));
         if(world.map[world.size-1][i]instanceof EmptySpace) this.world.exit.add(new Exit(l,world.size-1,i ,"right",x,y));
         if(world.map[0][i]instanceof EmptySpace) this.world.exit.add(new Exit(l,0,i ,"left",x,y));

      }

   }

   //On vérifie si une entrée existe dans la direction indiquée
   public Boolean entryCheck(String direction){
      if((direction.equals("up")) && (this.entryDown() >= 0)){
         return true;
      }
      if((direction.equals("down")) && (this.entryUp() >= 0)){
         return true;
      }
      if((direction.equals("left")) && (this.entryRight() >= 0)){
         return true;
      }
      if((direction.equals("right")) && (this.entryLeft() >= 0)){
         return true;
      }
      return false;
   }
}