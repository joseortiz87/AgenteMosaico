package mx.com.joortizs.agent.multi;

import java.util.LinkedList;
import java.util.Random;

import mx.com.joortizs.enviroment.GridMapEnviroment;
import mx.com.joortizs.enviroment.GridPoint;

public class RutaAgenteGrid {
	private int stepsx, stepsy; //NUMERO DE PASOS EN CADA EJE CON SIGNO
	private int score; //SCORE DE LA RUTA
	GridPoint origen, meta; //PUNTO INICIAL DE PARTIDA Y META
	LinkedList<GridPoint> ruta = new LinkedList<GridPoint>();
	private final GridMapEnviroment gridMapEnviroment;
	private boolean solved;
	private int heurx,heury;
	
	//Constructor
	public RutaAgenteGrid(GridMapEnviroment gridMapEnviroment,GridPoint origen, GridPoint meta){
		this.origen = origen;
		this.meta = meta;
		this.gridMapEnviroment = gridMapEnviroment;
		this.stepsx = this.calculaStepsX(origen.getX(),meta.getX());
		this.stepsy = this.calculaStepsY(origen.getY(),meta.getY());
		this.solved = this.BackTrackingRuta();
		this.score = ruta.size();
	}
	
	//Constructor 2
	public RutaAgenteGrid(GridPoint origen, GridPoint meta){
		this.origen = origen;
		this.meta = meta;
		this.gridMapEnviroment = null;
		this.stepsx = this.calculaStepsX(origen.getX(),meta.getX());
		this.stepsy = this.calculaStepsY(origen.getY(),meta.getY());
		this.score = this.calculaScoreHeur();
		this.solved = true;
	}
	
	public String toString(){
		return "Encontro ruta: " + this.solved  + "\nScore: " + this.score + "\nOrigen: " + this.origen + "\nMeta: " + this.meta + "\nRuta: " + this.ruta.toString();
	}
	
	//SIRVE COMO HEURISTICA PARA APROXIMAR UBICACION DE LA META X
	public int calculaStepsX(int orgx, int mtx){
		if(orgx > mtx){
			return (orgx-mtx);
		}else{
			if( mtx > orgx ){
				return -(mtx-orgx);
			}else{
				return 0;
			}
		}
	}
	
	//SIRVE COMO HEURISTICA PARA APROXIMAR UBICACION DE LA META Y
	public int calculaStepsY(int orgy, int mty){
		if(orgy > mty){
			return -(orgy-mty);
		}else{
			if(mty > orgy){
				return (mty-orgy);
			}else{
				return 0;
			}
		}
	}
	
	//BUSCAMOS LA RUTA LIBRE MAS CERCANA CON BACKTRACKING
	public boolean BackTrackingRuta(){
		LinkedList<GridPoint> puntosVisitados = new LinkedList<GridPoint>();
		LinkedList<GridPoint> puntosPosibles = new LinkedList<GridPoint>();
		GridPoint temp;
		GridPoint actual = origen;
		puntosPosibles.addFirst(origen);
		if(origen.isPointEqual(meta)){
			this.ruta = puntosPosibles;
			return true;
		}
		
		while(puntosPosibles.size() != 0){
			actual = puntosPosibles.getFirst();
			puntosVisitados.addFirst(actual);  //evitar loops
			temp = getNuevosPosibles(actual,puntosVisitados);
			if(temp == null){ //ES UNA HOJA
				if( actual.isPointEqual(meta) ){  //PRUEBA SI ES META
					this.ruta = puntosPosibles;
					return true;
				}else
					puntosPosibles.removeFirst();
			}else{
				if( temp != null ) //TIENE NODOS HIJOS POR EXPLORAR
					puntosPosibles.addFirst(temp);
			}
		}
		
		return false;
	}
	
	//GENERA NUEVOS NODOS QUE SE PUEDEN EXPLORAR
	//PONE PRIMERO LOS NODOS QUE PODRIAN RESULTAR EN CAMINOS MAS CORTOS
	//SE PUEDE DECIR QUE ES UNA HEURISTICA TAMBIEN SE PUEDE HACER DE FORMA ALEATORIA
	public GridPoint getNuevosPosibles(GridPoint actual,LinkedList<GridPoint> visitados){
		int tempx, tempy;
		heurx = 0;
		heury = 0;
		Random rand = new Random();
		 int offset = 0;
		
		if( !actual.isPointEqual(meta) ){
			LinkedList<GridPoint> posibles = new LinkedList<GridPoint>();
			posibles.addLast(new GridPoint(gridMapEnviroment.decreasePossX(actual.getX()),actual.getY())); // X PARA ARRIBA
			posibles.addLast(new GridPoint(gridMapEnviroment.increasePossX(actual.getX()),actual.getY())); // X PARA ABAJO
			posibles.addLast(new GridPoint(actual.getX(),gridMapEnviroment.increasePossY(actual.getY()))); // Y DERECHA
			posibles.addLast(new GridPoint(actual.getX(),gridMapEnviroment.decreasePossY(actual.getY()))); // Y IZQUIERDA
	
			if( 100-rand.nextInt(101) <= 25 ){
				int randomnumber = rand.nextInt(posibles.size());
				tempx = posibles.get(randomnumber).getX();
				tempy = posibles.get(randomnumber).getY();
				if( gridMapEnviroment.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
					return posibles.get(randomnumber);
				else{
					posibles.remove(randomnumber);
					offset++;
				}
			}
			
			
			 
			//CASOS PARA X  ARRIBA
			if( stepsx > 0 && heurx < stepsx ){
				tempx = posibles.get(0).getX();
				tempy = posibles.get(0).getY();
				if( gridMapEnviroment.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) ){
					heurx++;
					return posibles.get(0);
				}else{
					posibles.remove(0);
					offset++;
				}
			}
			
			//ABAJO
			if( stepsx < 0 && -heurx > stepsx ){
				tempx = posibles.get(1).getX();
				tempy = posibles.get(1).getY();
				if( gridMapEnviroment.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) ){
					heurx++;
					return posibles.get(1);
				}else{
					posibles.remove(1);
					offset++;
				}
			}
			
			//CASOS PARA Y  DERECHA
			if( stepsy > 0 && heury < stepsy ){
				tempx = posibles.get(2-offset).getX();
				tempy = posibles.get(2-offset).getY();
				if( gridMapEnviroment.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) ){
					heury++;
					return posibles.get(2-offset);
				}else{
					posibles.remove(2-offset);
				}
			}
			
			//IZQUIERDA
			if( stepsy < 0 && -heury > stepsy ){
				tempx = posibles.get(3-offset).getX();
				tempy = posibles.get(3-offset).getY();
				if( gridMapEnviroment.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) ){
					heury++;
					return posibles.get(3-offset);
				}else{
					posibles.remove(3-offset);
				}
			}
			
			
			//SI NO TOMA UN NODO ALEATORIO LIBRE
			while(posibles.size() > 0){
				int randomnumber = rand.nextInt(posibles.size());
				tempx = posibles.get(randomnumber).getX();
				tempy = posibles.get(randomnumber).getY();
				if( gridMapEnviroment.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
					return posibles.get(randomnumber);
				else
					posibles.remove(randomnumber);
			}
					
		} //NO ES META

		return null;
	}
	
	//BUSCA QUE EL PUNTO NO SE HAYA VISITADO EVITANDO LOOPS 
	public boolean isGridPointVisited(int px,int py,LinkedList<GridPoint> visitados){
		GridPoint tempp = new GridPoint(px,py);
		for(GridPoint visitado : visitados){
			if(visitado.isPointEqual(tempp)) return true;
		}
		return false;
	}
	
	//RUTA
	public LinkedList<GridPoint> getRuta(){
		return ruta;
	}
	
	public int getScoreRuta(){
		return score;
	}
	
	public GridPoint getPuntoOrigen(){
		return origen;
	}
	
	public GridPoint getPuntoMeta(){
		return meta;
	}
	
	public int calculaScore(){
		return this.ruta.size();
	}
	
	public boolean isSolved() {
		return solved;
	}

	public GridMapEnviroment getGridMapEnviroment() {
		return gridMapEnviroment;
	}

	public int calculaScoreHeur(){
		int sum = 0;
		if( stepsx < 0 ) sum += -(stepsx); else sum += stepsx;
		if( stepsy < 0 ) sum += -(stepsy); else sum += stepsy;
		return sum;
	}
	
	public static void main(String [] args){
		
	}

}
