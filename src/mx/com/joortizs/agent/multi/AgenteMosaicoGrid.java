package mx.com.joortizs.agent.multi;

import java.util.ArrayList;
import java.util.LinkedList;

import mx.com.joortizs.enviroment.GridMapEnviroment;
import mx.com.joortizs.enviroment.GridPoint;

public class AgenteMosaicoGrid {

	private final GridMapEnviroment gridMapEnviroment;
	private static final int NUMBER_AGENTS = 2;
	private ArrayList<Agente> agentes = new ArrayList<Agente>();
	private int globalScore;
	
	public AgenteMosaicoGrid(){
		gridMapEnviroment= new GridMapEnviroment();
	}
	
	public GridPoint getIniposs(){
		return gridMapEnviroment.getIniposs();
	}
	
	private class Agente extends Thread{
		private GridPoint currentposs;
		private int agentScore;
		
		public Agente(){
			this.currentposs = getIniposs();
			this.agentScore = 0;
		}
		
		//Calcula Ruta mas cercana entre un punto INICIO y un conjunto de puntos METAS	
		//Encuentra Mosaico mas cercano desde ruta actual del agente
		private RutaAgenteGrid getMosaicoMasCercano(GridPoint inicio,LinkedList<GridPoint> metas){
			LinkedList<RutaAgenteGrid> rutas = new LinkedList<RutaAgenteGrid>();
			int index = 0;
			for(GridPoint meta : metas){
				RutaAgenteGrid tempstep = new RutaAgenteGrid(inicio,meta);
				if(tempstep.isSolved()){ 
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
		private RutaAgenteGrid getHoyoMasCercano(GridPoint inicio,LinkedList<GridPoint> metas){
			LinkedList<RutaAgenteGrid> rutas = new LinkedList<RutaAgenteGrid>();
			int index = 0;
			for(GridPoint meta : metas){
				RutaAgenteGrid tempstep = null;
				char tmpch = gridMapEnviroment.getValue(meta.getX(), meta.getY());
				if( tmpch == '1' || tmpch == '2' ) gridMapEnviroment.changeValPoint(meta.getX(), meta.getY(),'0');
				tempstep = new RutaAgenteGrid(gridMapEnviroment,inicio,meta);
				if( tmpch == '1' || tmpch == '2' ) gridMapEnviroment.changeValPoint(meta.getX(), meta.getY(), tmpch);
				//System.out.println("\nRUTAS HOYO: " + tempstep.toString());
				if(tempstep != null && tempstep.isSolved()){
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
		private int getperfilX(int x1,int x2){
			if( x1 > x2 ){
				return gridMapEnviroment.increasePossX(x1);
			}else if( x1 < x2 ){
				return gridMapEnviroment.decreasePossX(x1);
			}else{
				return x1;
			}
		}
		
		private int getperfilY(int y1,int y2){
			if( y1 > y2 ){
				return gridMapEnviroment.increasePossY(y1);
			}else if( y1 < y2 ){
				return gridMapEnviroment.decreasePossY(y1);
			}else{
				return y1;
			}
		}
		
		
		//Perfilamos Robot para mover mosaico basado en la ubicacion de hoyo
		private boolean perfilarRobotMosaico(RutaAgenteGrid rutabestHoyo){
			int startx,starty;
			LinkedList<GridPoint> ruta = rutabestHoyo.getRuta();
			GridPoint possMosaico = ruta.pollLast();
			GridPoint posstemp;
			RutaAgenteGrid perfil;
			//Colocar Robot para Mover Mosaico
			//MOVIMIENTO EN X,Y siguiendo la RUTA
			while(ruta.size() != 0){
				posstemp = ruta.pollLast();
				startx = getperfilX(possMosaico.getX(),posstemp.getX());
				starty = getperfilY(possMosaico.getY(),posstemp.getY());
				//RUTA A PERFIL
				perfil = new RutaAgenteGrid(gridMapEnviroment,currentposs,new GridPoint(startx,starty));
				System.out.println("\nPerfil: " + perfil.toString());
				if(perfil.isSolved()){
					moverRobotRuta(perfil);
					moverMosaico(possMosaico,posstemp);
				}else{
					return false;
				}
				possMosaico = posstemp;
			}
			return true;
		}
		
		//MUEVE MOSAICO CUANDO LO MUEVE EL AGENTE
		private boolean moverMosaico(GridPoint actual,GridPoint next){
			actualizaPosicion(actual);	
			actualizaPosicionMosaico(actual,next);
			return true;
		}
		
		//mueve el agente de una ubicacion a otra siguiendo un camino
		private boolean moverRobotRuta(RutaAgenteGrid ruta){
			LinkedList<GridPoint> tempRuta = ruta.getRuta();
			tempRuta.removeLast(); //posicion actual
			while(tempRuta.size() != 0){
				actualizaPosicion(tempRuta.pollLast());
			}
			return true;
		}
		
		//ACTUALIZA POSICION
		private void actualizaPosicion(GridPoint newp){
			gridMapEnviroment.changeValPoint(currentposs.getX(), currentposs.getY(), '0');
			currentposs = newp;
			gridMapEnviroment.changeValPoint(currentposs.getX(), currentposs.getY(), 'R');
			gridMapEnviroment.repaintMap();
			delayThread();
		}
		
		private void actualizaPosicionMosaico(GridPoint actual,GridPoint next){
			if( gridMapEnviroment.isSpaceHoyo(next.getX(), next.getY()) ){
				gridMapEnviroment.decreaseMosHoy(actual, next); //ELIMINA EL MOSAICO Y EL HOYO Y AGENTE EN POSICION ACTUAL
				agentScore = gridMapEnviroment.getScore();
			}else{
				gridMapEnviroment.moveMosaico(actual, next);  //PASA MOSAICO A NEXT Y AGENTE EN POSICION ACTUAL
			}
			gridMapEnviroment.repaintMap();
			delayThread();
		}

		private void delayThread(){
	        try {
	            sleep(500);
	        } catch (InterruptedException ex) {
	        	
	        }
		}
		
		//IMPRIME DATOS AGENTE
		public String toString(){
			return "Score logrado: " + agentScore + "%\n" + gridMapEnviroment.toString();
		}
		
		//MOVIMIENTOS DEL AGENTE
		//EJECUTA EL THREAD DEL AGENTE
		@Override
		public void run(){
			actualizaPosicion(currentposs);
	        int countHoyos = 1;
			while(agentScore <= 95 && countHoyos > 0 ){
				RutaAgenteGrid rutamos1;
				rutamos1 = getMosaicoMasCercano(currentposs,gridMapEnviroment.getMosaicos());
				if( rutamos1 != null ){
					RutaAgenteGrid rutahoy1;
					rutahoy1 = getHoyoMasCercano(rutamos1.meta,gridMapEnviroment.getHoyos());
					if( rutahoy1 != null ){
						System.out.println("\n" + getId() + "- RUTA MOSAICO HOYO: \n" + rutahoy1.toString());
						perfilarRobotMosaico(rutahoy1);  //MUEVE EL MOSAICO
						System.out.println("\nCurrent Score: " + agentScore);
					}
				}
				countHoyos = gridMapEnviroment.getCountHoyos();
			}
			System.out.println(toString());
		}
	}
	
	
	public int getGlobalScore() {
		return globalScore;
	}

	public void setGlobalScore(int globalScore) {
		this.globalScore = globalScore;
	}

	public ArrayList<Agente> getAgentes() {
		return agentes;
	}
	
	public void startAgents(){
		for(int i=0;i<NUMBER_AGENTS;i++){
			agentes.add(new Agente());
		}
		long startTime = System.currentTimeMillis();
		for(Agente agent : agentes){
			agent.run();
		}
		for(Agente agent : agentes){
			while(agent.isAlive()){
				try{
					agent.join();
				}catch(Exception e){
					
				}
			}
		}
		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("\nTotal elapsed time:  " + (elapsedTime/1000.0) + " seconds.\n");
	}
	
	public boolean isMapLoaded(){
		return gridMapEnviroment.isMapLoaded();
	}


	public static void main(String args[]){
		System.out.println("Start agent..");
		AgenteMosaicoGrid testGrid = new AgenteMosaicoGrid();
		while(!testGrid.isMapLoaded()){
			
		}
		testGrid.startAgents();
	}
}
