package mx.com.joortiz.agent.single;
import java.util.LinkedList;
import java.util.logging.*;


public class AgenteGrid implements Runnable{
	GridPoint currentposs;
	EnvMap ambiente;
	int score;
	int numHoyosIni;
	public Skeleton sk;
	private Thread runner;
	
	//Constructor
	AgenteGrid(EnvMap ambiente){
		this.ambiente = ambiente;
		this.currentposs = ambiente.iniposs;
		this.score = 0;
		this.numHoyosIni = ambiente.getNumeroHoyos();
	}
	
	//Calcula Ruta mas cercana entre un punto INICIO y un conjunto de puntos METAS	
	//Encuentra Mosaico mas cercano desde ruta actual del agente
	Steps getMosaicoMasCercano(GridPoint inicio,LinkedList<GridPoint> metas){
		LinkedList<Steps> rutas = new LinkedList<Steps>();
		int index = 0;
		for(GridPoint meta : metas){
			Steps tempstep = new Steps(inicio,meta);
			if(tempstep.solved){ 
				if( index == 0 ){
						rutas.addFirst(tempstep);
						index++;
				}else{
					if( tempstep.getScoreRuta() < rutas.getFirst().getScoreRuta() ){
							rutas.addFirst(tempstep);  //NUEVA RUTA MAS CORTA
					}
				}
			}
		}
		if( rutas.size() > 0 )
			return rutas.getFirst();
		else
			return null;
	}
	
	//Encuentra Hoyo mas cercano desde un mosaico
	Steps getHoyoMasCercano(GridPoint inicio,LinkedList<GridPoint> metas){
		LinkedList<Steps> rutas = new LinkedList<Steps>();
		int index = 0;
		for(GridPoint meta : metas){
			char tmpch = ambiente.getValue(meta.x, meta.y);
			if( tmpch == '1' || tmpch == '2' ) ambiente.changeValPoint(meta.x, meta.y,'0');
			Steps tempstep = new Steps(ambiente,inicio,meta);
			if( tmpch == '1' || tmpch == '2' ) ambiente.changeValPoint(meta.x, meta.y, tmpch);
			//System.out.println("\nRUTAS HOYO: " + tempstep.toString());
			if(tempstep.solved){
				if( index == 0 ){
					rutas.addFirst(tempstep);
					index++;
				}else{
					if( tempstep.getScoreRuta() < rutas.getFirst().getScoreRuta() ){
						rutas.addFirst(tempstep);  //NUEVA RUTA MAS CORTA
					}
				}
			}
		}
		if( rutas.size() > 0 )
			return rutas.getFirst();
		else
			return null;
	}
	
	//SIRVE PARA UBICAR DONDE PONERSE PARA MOVER MOSAICO EN XY	
	int getperfilX(int x1,int x2){
		if( x1 > x2 ){
			return ambiente.increasePossX(x1);
		}else if( x1 < x2 ){
			return ambiente.decreasePossX(x1);
		}else{
			return x1;
		}
	}
	
	int getperfilY(int y1,int y2){
		if( y1 > y2 ){
			return ambiente.increasePossY(y1);
		}else if( y1 < y2 ){
			return ambiente.decreasePossY(y1);
		}else{
			return y1;
		}
	}
	
	
	//Perfilamos Robot para mover mosaico basado en la ubicacion de hoyo
	boolean perfilarRobotMosaico(Steps rutabestHoyo,Skeleton skp){
		int startx,starty;
		LinkedList<GridPoint> ruta = rutabestHoyo.getRuta();
		GridPoint possMosaico = ruta.pollLast();
		GridPoint posstemp;
		Steps perfil;
		
		//Colocar Robot para Mover Mosaico
		//MOVIMIENTO EN X,Y siguiendo la RUTA
		while(ruta.size() != 0){
			posstemp = ruta.pollLast();
			startx = getperfilX(possMosaico.x,posstemp.x);
			starty = getperfilY(possMosaico.y,posstemp.y);
			//RUTA A PERFIL
			perfil = new Steps(ambiente,currentposs,new GridPoint(startx,starty));
			System.out.println("\nPerfil: " + perfil.toString());
			if(perfil.solved){
				moverRobotRuta(perfil,skp);
				moverMosaico(possMosaico,posstemp,skp);
			}else{
				return false;
			}
			possMosaico = posstemp;
		}
		return true;
	}
	
	//MUEVE MOSAICO CUANDO LO MUEVE EL AGENTE
	boolean moverMosaico(GridPoint actual,GridPoint next,Skeleton skp){
		actualizaPosicion(actual,skp);	
		actualizaPosicionMosaico(actual,next,skp);
		return true;
	}
	
	//mueve el agente de una ubicacion a otra siguiendo un camino
	boolean moverRobotRuta(Steps ruta,Skeleton skp){
		LinkedList<GridPoint> tempRuta = ruta.getRuta();
		tempRuta.removeLast(); //posicion actual
		while(tempRuta.size() != 0){
			actualizaPosicion(tempRuta.pollLast(),skp);
		}
		return true;
	}
	
	//ACTUALIZA POSICION
	void actualizaPosicion(GridPoint newp,Skeleton skp){
		ambiente.changeValPoint(currentposs.x, currentposs.y, '0');
		currentposs = newp;
		ambiente.changeValPoint(currentposs.x, currentposs.y, 'R');
		skp.repaintMap();
		delayThread();
	}
	
	void actualizaPosicionMosaico(GridPoint actual,GridPoint next,Skeleton skp){
		if( ambiente.isSpaceHoyo(next.x, next.y) ){
			ambiente.decreaseMosHoy(actual, next); //ELIMINA EL MOSAICO Y EL HOYO Y AGENTE EN POSICION ACTUAL
			actualizaScore();
		}else{
			ambiente.moveMosaico(actual, next);  //PASA MOSAICO A NEXT Y AGENTE EN POSICION ACTUAL
		}
		skp.repaintMap();
		delayThread();
	}

	//ACTUALIZA LA UTILIDAD
	void actualizaScore(){
		this.score = 100 - (((ambiente.getNumeroHoyos()*100)/this.numHoyosIni));
	}
	
	//MOVIMIENTOS DEL AGENTE
	//EJECUTA EL THREAD DEL AGENTE
	@Override
	public void run(){
        this.sk = new Skeleton(this.ambiente);
        this.sk.setVisible(true);
        actualizaPosicion(currentposs,this.sk);
		while(this.score <= 95 && ambiente.hoyos > 0 ){
			Steps rutamos1 = getMosaicoMasCercano(currentposs,ambiente.mosaicosposs);
			if( rutamos1 != null ){
				Steps rutahoy1 = getHoyoMasCercano(rutamos1.meta,ambiente.hoyosposs);
				if( rutahoy1 != null ){
					System.out.println("\nRUTA MOSAICO HOYO: \n" + rutahoy1.toString());
					perfilarRobotMosaico(rutahoy1,this.sk);  //MUEVE EL MOSAICO
					System.out.println("\nCurrent Score: " + this.score);
				}
			}
		}
		System.out.println(toString());
	}
	
	void initThread(){
        this.runner = new Thread(this);
        this.runner.start();  //start thread
	}
	
	void delayThread(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
             Logger.getLogger(Surface.class.getName()).log(Level.SEVERE, 
                     null, ex);
        }
	}
	
	//IMPRIME DATOS AGENTE
	public String toString(){
		return "Score logrado: " + this.score + "%\n" + ambiente.toString();
	}
	
	//INTANCIA DEL AGANETE EN EL AMBIENTE MAP.TXT
	public static void main(String [] args){
		AgenteGrid ag1 = new AgenteGrid(new EnvMap("Map.txt"));
		ag1.initThread();
		//System.out.println("\nResumen\n" + ag1.toString());
	}
	
}
