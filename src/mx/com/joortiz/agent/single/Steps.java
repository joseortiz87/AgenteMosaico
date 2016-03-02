package mx.com.joortiz.agent.single;
import java.util.*;

public class Steps {
	int stepsx, stepsy; //NUMERO DE PASOS EN CADA EJE CON SIGNO
	int score; //SCORE DE LA RUTA
	GridPoint origen, meta; //PUNTO INICIAL DE PARTIDA Y META
	LinkedList<GridPoint> ruta = new LinkedList<GridPoint>();
	EnvMap map;
	boolean solved;
	int heurx,heury;
	
	//Constructor
	Steps(EnvMap map,GridPoint origen, GridPoint meta){
		this.origen = origen;
		this.meta = meta;
		this.map = map;
		this.stepsx = this.calculaStepsX(origen.x,meta.x);
		this.stepsy = this.calculaStepsY(origen.y,meta.y);
		this.solved = this.BackTrackingRuta();
		this.score = ruta.size();
	}
	
	//Constructor 2
	Steps(GridPoint origen, GridPoint meta){
		this.origen = origen;
		this.meta = meta;
		this.stepsx = this.calculaStepsX(origen.x,meta.x);
		this.stepsy = this.calculaStepsY(origen.y,meta.y);
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
			posibles.addLast(new GridPoint(map.decreasePossX(actual.x),actual.y)); // X PARA ARRIBA
			posibles.addLast(new GridPoint(map.increasePossX(actual.x),actual.y)); // X PARA ABAJO
			posibles.addLast(new GridPoint(actual.x,map.increasePossY(actual.y))); // Y DERECHA
			posibles.addLast(new GridPoint(actual.x,map.decreasePossY(actual.y))); // Y IZQUIERDA
	
			if( 100-rand.nextInt(101) <= 25 ){
				int randomnumber = rand.nextInt(posibles.size());
				tempx = posibles.get(randomnumber).x;
				tempy = posibles.get(randomnumber).y;
				if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
					return posibles.get(randomnumber);
				else{
					posibles.remove(randomnumber);
					offset++;
				}
			}
			
			
			 
			//CASOS PARA X  ARRIBA
			if( stepsx > 0 && heurx < stepsx ){
				tempx = posibles.get(0).x;
				tempy = posibles.get(0).y;
				if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) ){
					heurx++;
					return posibles.get(0);
				}else{
					posibles.remove(0);
					offset++;
				}
			}
			
			//ABAJO
			if( stepsx < 0 && -heurx > stepsx ){
				tempx = posibles.get(1).x;
				tempy = posibles.get(1).y;
				if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) ){
					heurx++;
					return posibles.get(1);
				}else{
					posibles.remove(1);
					offset++;
				}
			}
			
			//CASOS PARA Y  DERECHA
			if( stepsy > 0 && heury < stepsy ){
				tempx = posibles.get(2-offset).x;
				tempy = posibles.get(2-offset).y;
				if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) ){
					heury++;
					return posibles.get(2-offset);
				}else{
					posibles.remove(2-offset);
				}
			}
			
			//IZQUIERDA
			if( stepsy < 0 && -heury > stepsy ){
				tempx = posibles.get(3-offset).x;
				tempy = posibles.get(3-offset).y;
				if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) ){
					heury++;
					return posibles.get(3-offset);
				}else{
					posibles.remove(3-offset);
				}
			}
			
			
			//SI NO TOMA UN NODO ALEATORIO LIBRE
			while(posibles.size() > 0){
				int randomnumber = rand.nextInt(posibles.size());
				tempx = posibles.get(randomnumber).x;
				tempy = posibles.get(randomnumber).y;
				if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
					return posibles.get(randomnumber);
				else
					posibles.remove(randomnumber);
			}
			
/*			
			//ABRIR PUNTOS X 
			if( stepsx != 0){  //ABRE PRIMERO EN DIRECCION DE LA META
				if(stepsx > 0){
					tempx = map.decreasePossX(actual.x);
					tempy = actual.y;
					if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
						posibles.addLast(new GridPoint(tempx,tempy));  //SELECCIONAMOS NODO DE ACUERDO A UBICACION
					
					tempx = map.increasePossX(actual.x);
					if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
						posibles.addLast(new GridPoint(tempx,tempy));  //SELECCIONAMOS NODO DE ACUERDO A UBICACION
					
				}
				
				if(stepsx < 0){
					tempx = map.increasePossX(actual.x);
					tempy = actual.y;
					if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
						return new GridPoint(tempx,tempy);  //SELECCIONAMOS NODO DE ACUERDO A UBICACION
					
					tempx = map.decreasePossX(actual.x);
					if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
						return new GridPoint(tempx,tempy);  //SELECCIONAMOS NODO DE ACUERDO A UBICACION
				}
			}else{
				//ABRIR PUNTOS Y PRIMERO SI STEPS EN X ES 0
				if(stepsy > 0){
					tempx = actual.x;
					tempy = map.increasePossY(actual.y);
					if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
						return new GridPoint(tempx,tempy); //SELECCIONAMOS NODO DE ACUERDO A UBICACION
					
					tempy = map.decreasePossY(actual.y);
					if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
						return new GridPoint(tempx,tempy); //SELECCIONAMOS NODO DE ACUERDO A UBICACION
				}
				
				if(stepsy < 0){
					tempx = actual.x;
					tempy = map.decreasePossY(actual.y);
					if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
						return new GridPoint(tempx,tempy); //SELECCIONAMOS NODO DE ACUERDO A UBICACION
					
					tempy = map.increasePossY(actual.y);
					if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
						return new GridPoint(tempx,tempy); //SELECCIONAMOS NODO DE ACUERDO A UBICACION
				}
				
				//DESPUES X POR SI LOS CAMINOS ESTAN CERRADOS
				tempx = map.decreasePossX(actual.x);
				tempy = actual.y;
				if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
					return new GridPoint(tempx,tempy);  //SELECCIONAMOS NODO DE ACUERDO A UBICACION
				
				tempx = map.increasePossX(actual.x);
				if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
					return new GridPoint(tempx,tempy);  //SELECCIONAMOS NODO DE ACUERDO A UBICACION
				
			}
			
			//ABRIR PUNTOS Y
			if(stepsy != 0){
				if(stepsy > 0){
					tempx = actual.x;
					tempy = map.increasePossY(actual.y);
					if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
						return new GridPoint(tempx,tempy); //SELECCIONAMOS NODO DE ACUERDO A UBICACION
					
					tempy = map.decreasePossY(actual.y);
					if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
						return new GridPoint(tempx,tempy); //SELECCIONAMOS NODO DE ACUERDO A UBICACION
				}
				
				if(stepsy < 0){
					tempx = actual.x;
					tempy = map.decreasePossY(actual.y);
					if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
						return new GridPoint(tempx,tempy); //SELECCIONAMOS NODO DE ACUERDO A UBICACION
					
					tempy = map.increasePossY(actual.y);
					if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
						return new GridPoint(tempx,tempy); //SELECCIONAMOS NODO DE ACUERDO A UBICACION
				}
			}else{
				//ABRIMOS PRIMERO X EN DIRECCION A ALA META
				if(stepsx > 0){
					tempx = map.decreasePossX(actual.x);
					tempy = actual.y;
					if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
						return new GridPoint(tempx,tempy);  //SELECCIONAMOS NODO DE ACUERDO A UBICACION
					
					tempx = map.increasePossX(actual.x);
					if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
						return new GridPoint(tempx,tempy);  //SELECCIONAMOS NODO DE ACUERDO A UBICACION
					
				}
				
				if(stepsx < 0){
					tempx = map.increasePossX(actual.x);
					tempy = actual.y;
					if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
						return new GridPoint(tempx,tempy);  //SELECCIONAMOS NODO DE ACUERDO A UBICACION
					
					tempx = map.decreasePossX(actual.x);
					if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
						return new GridPoint(tempx,tempy);  //SELECCIONAMOS NODO DE ACUERDO A UBICACION
				}
				
				//DESPUES Y POR SI LOS CAMINOS ESTAN CERRADOS
				tempx = actual.x;
				tempy = map.increasePossY(actual.y);
				if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
					return new GridPoint(tempx,tempy); //SELECCIONAMOS NODO DE ACUERDO A UBICACION
				
				tempy = map.decreasePossY(actual.y);
				if( map.isSpaceFree(tempx, tempy) && !isGridPointVisited(tempx,tempy,visitados) )
					return new GridPoint(tempx,tempy); //SELECCIONAMOS NODO DE ACUERDO A UBICACION
				
			}
  */
		
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
	
	public int calculaScoreHeur(){
		int sum = 0;
		if( stepsx < 0 ) sum += -(stepsx); else sum += stepsx;
		if( stepsy < 0 ) sum += -(stepsy); else sum += stepsy;
		return sum;
	}
	
	public static void main(String [] args){
		EnvMap map = new EnvMap("Map.txt");
		System.out.println(map.toString());
		Steps stp1 = new Steps(map,new GridPoint(2,1),new GridPoint(4,2));
		System.out.println(stp1.toString());
	}
	
}
