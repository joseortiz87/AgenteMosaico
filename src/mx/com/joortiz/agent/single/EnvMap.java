package mx.com.joortiz.agent.single;
import java.io.*;
import java.util.*;

public class EnvMap {
	int rows, columns;
	int hoyos, mosaicos;
	GridPoint iniposs;
	String map;
	LinkedList<GridPoint> hoyosposs = new LinkedList<GridPoint>();
	LinkedList<GridPoint> mosaicosposs = new LinkedList<GridPoint>();
	
	//Constructor
	EnvMap(String file){
		this.hoyos = 0;
		this.mosaicos = 0;
		int count = 0;
		int currentrow = 0;
		int currentcolumn = 0;
		StringBuilder temmap = new StringBuilder();
		BufferedReader br = null;
		
	    try {
	    	br = new BufferedReader(new FileReader(file));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	        	if( count == 0 ){
	        		String [] tempstr = line.split(" "); 
	        		this.rows = Integer.parseInt(tempstr[0]);
	        		this.columns = Integer.parseInt(tempstr[1]);
	        	}else{
	        		if( count == this.rows +1){
	        			String [] tempstr = line.split(" ");
	        			iniposs = new GridPoint(Integer.parseInt(tempstr[0]),Integer.parseInt(tempstr[1]));
	        		}else{
	    	            sb.append(line);
	    	            sb.append('\n');
	        		}
	        	}
	            line = br.readLine();
	            count++;
	        }
	        this.map = sb.toString();
	        
	        for(String row: this.map.split("\n")){
	        	for(int i=0;i<row.length();i++){
	        		if( row.charAt(i) == '1' ){
	        			this.hoyos++;
	        			hoyosposs.add(new GridPoint(currentrow,currentcolumn));
	        		}
	        		if( row.charAt(i) == '2'){
	        			this.mosaicos++;
	        			mosaicosposs.add(new GridPoint(currentrow,currentcolumn));
	        		}
	        		if( row.charAt(i) != ' ' ){
	        			temmap.append(row.charAt(i));
	        			currentcolumn++;
	        		}
	        	}
	        	temmap.append('\n');
	        	currentcolumn = 0;
	        	currentrow++;
	        }
	        
	        this.map = temmap.toString();
	        
	    } catch (Exception ex){
	    	System.out.println(ex);
	    	ex.printStackTrace();
	    } finally {
	        try{ if(br != null) br.close();} catch(Exception e){ System.out.println(e); }
	    } 
	}
	
	int getNumberofRows(){
		return rows;
	}
	
	int getNumberofColumns(){
		return columns;
	}
	
	int getNumeroMosaicos(){
		return mosaicos;
	}
	
	int getNumeroHoyos(){
		return hoyos;
	}
	
	public int increasePossX(int x){
		int temp = x;
		if( x == rows-1 )
			return 0;
		else
			return ++temp;
	}
	
	public int decreasePossX(int x){
		int temp = x;
		if( x == 0 )
			return rows-1;
		else
			return --temp;
	}
	
	public int increasePossY(int y){
		int temp = y;
		if( y == columns-1 )
			return 0;
		else
			return ++temp;
	}
	
	public int decreasePossY(int y){
		int temp = y;
		if( y == 0 )
			return columns-1;
		else
			return --temp;
	}
	
	public boolean isSpaceFree(int x,int y){
		char temp = map.charAt((x*(columns+1))+y);
		if( temp == '0' )
			return true;
		else
			return false;
	}
	
	public boolean isSpaceMosaico(int x,int y){
		char temp = map.charAt((x*(columns+1))+y);
		if( temp == '2')
			return true;
		else
			return false;
	}
	
	public boolean isSpaceHoyo(int x,int y){
		char temp = map.charAt((x*(columns+1))+y);
		if( temp == '1')
			return true;
		else
			return false;
	}
	
	public boolean isSpaceRobot(int x,int y){
		char temp = map.charAt((x*(columns+1))+y);
		if( temp == 'R')
			return true;
		else
			return false;
	}
	
	public char getValue(int x, int y){
		char temp = map.charAt((x*(columns+1))+y);
		return temp;
	}
	
	public void changeValPoint(int x, int y,char val){
		char [] temp = map.toCharArray();
		temp[(x*(columns+1))+y] = val;
		map = new String(temp);
	}
	
	public void decreaseMosHoy(GridPoint mosc, GridPoint hoy){
		changeValPoint(mosc.x, mosc.y, 'R');
		changeValPoint(hoy.x, hoy.y, '0');
		this.mosaicos = this.mosaicos-1;
		this.hoyos = this.hoyos-1;
		removeMosaico(mosc);
		removeHoyo(hoy);
	}
	
	public void moveMosaico(GridPoint mosc,GridPoint newmosc){
		int i = 0;
		changeValPoint(mosc.x, mosc.y, 'R');
		changeValPoint(newmosc.x, newmosc.y, '2');
		for(GridPoint itm : mosaicosposs){
			if( itm.isPointEqual(mosc)) break;
			++i;
		}
		mosaicosposs.remove(i);
		mosaicosposs.add(newmosc);
	}
	
	void removeMosaico(GridPoint mosc){
		int i = 0;
		for(GridPoint itm : mosaicosposs){
			if( itm.isPointEqual(mosc)) break;
			++i;
		}
		mosaicosposs.remove(i);
	}
	
	void removeHoyo(GridPoint hoy){
		int i = 0;
		for(GridPoint itm : hoyosposs){
			if( itm.isPointEqual(hoy)) break;
			++i;
		}
		hoyosposs.remove(i);
	}
	
	//regresa lista de mosaicos
	LinkedList<GridPoint> getMosaicos(){
		return mosaicosposs;
	}
	
	//regresa lista de hoyos
	LinkedList<GridPoint> getHoyos(){
		return hoyosposs;
	}
	
	//IMPRIME MAPA
	public String toString(){
		return "Tamo del grid: " + this.rows + "x" + this.columns + "\n" + "Number of hoyos: " + this.hoyosposs.size() + "\n" + "Numero of mosaicos: " + this.mosaicosposs.size() + " \nPunto inicial: " + this.iniposs.toString() + "\n" + this.map;
	}
	
	public static void main(String [] args){
		EnvMap map = new EnvMap("Map.txt");
		System.out.println(map.toString());
		int m = map.increasePossX(1);
		System.out.println("\n" + m);
	
        Skeleton sk = new Skeleton(map);
        sk.setVisible(true);
        sk.repaint(1);
		
		/*
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                EnvMap map = new EnvMap("Map.txt");
                Skeleton sk = new Skeleton(map);
                sk.setVisible(true);
            }
        });
        */
		
	}
	
}
