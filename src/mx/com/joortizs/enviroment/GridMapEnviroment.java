package mx.com.joortizs.enviroment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;


public class GridMapEnviroment {

	private int rows, columns;
	private int numHoyosIni;
	private GridPoint iniposs;
	private String map;
	private LinkedList<GridPoint> hoyosposs = new LinkedList<GridPoint>();
	private LinkedList<GridPoint> mosaicosposs = new LinkedList<GridPoint>();
	
	
	/*
	 * FOR UI
	 * */
	private final JFrame mainFrame;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem menuItem;
	private final Surface surface;
	
	private File file;
	private boolean isMapLoaded = false;
	
    public GridMapEnviroment() {
    	this.mainFrame = new JFrame();
    	this.surface = new Surface();
        initUI();
        showFileChooserDemo();
    }
    
	/*
	 * LEE EL ARCHIVO
	 * CONSTRUYE EL GRID
	 * */
	private void buildGridFromFile(){
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
	        			hoyosposs.add(new GridPoint(currentrow,currentcolumn));
	        		}
	        		if( row.charAt(i) == '2'){
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
	        
	        numHoyosIni = getCountHoyos();
	        map = temmap.toString();
	        isMapLoaded = true;
	        repaintMap();
	        
	    } catch (Exception ex){
	    	System.out.println(ex);
	    	ex.printStackTrace();
	    } finally {
	        try{ if(br != null) br.close();} catch(Exception e){ System.out.println(e); }
	    } 
	}

    private void initUI() {

    	this.menuBar = new JMenuBar();
    	
    	this.menu = new JMenu("Menu");
    	menuBar.add(menu);
    	
    	this.menuItem = new JMenuItem("Load Map",KeyEvent.VK_T);
    	menu.add(menuItem);
    	
    	mainFrame.setTitle("Tarea 1 Problema Mosaicos Hoyos");
    	mainFrame.setSize(700, 800);
    	mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	mainFrame.setLocationRelativeTo(null);
    	mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
             }        
         });
        
    	mainFrame.setJMenuBar(menuBar);
        mainFrame.add(surface,BorderLayout.CENTER);
        mainFrame.setVisible(true);  
    }
    
    synchronized public void repaintMap(){
    	this.surface.repaint();
    }
       
    private void showFileChooserDemo(){
    	
        final JFileChooser  fileDialog = new JFileChooser();
        //JButton showFileDialogButton = new JButton("Open File");
        menuItem.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
              int returnVal = fileDialog.showOpenDialog(mainFrame);
              if (returnVal == JFileChooser.APPROVE_OPTION) {
            	  file = fileDialog.getSelectedFile();
            	  buildGridFromFile();  //BUILD GRID
              }
              else{
                 //statusLabel.setText("Open command cancelled by user." );           
              }      
           }
        });
    }
    
    /*
     * PAINT GRID
     * */
    class Surface extends JPanel {

    	private static final long serialVersionUID = 1L;
    
        private void doDrawing(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            if( map != null ){
    	        g2d.drawString("Numero de Hoyos que restan: " + getCountHoyos(), 10, 10);
    	        g2d.setColor(new Color(102, 102, 153));
    	        for(int i=0;i<getRows();++i){
    	        	for(int j=0;j<getColumns();++j){
    	        		if( isSpaceHoyo(j, i) ){
    	        	        g2d.setColor(new Color(0, 0, 0));
    	        		}else{
    	        			if( isSpaceMosaico(j, i) ){
    	        				g2d.setColor(new Color(220,20,60));
    	        			}else{
    	        				if( isSpaceRobot(j, i) ){  //ROBOT
    	        					g2d.setColor(new Color(255,69,0));
    	        				}else{
    	        					g2d.setColor(new Color(102, 102, 153));
    	        				}
    	        			}
    	        		}
    	        		g2d.fillRect((25+(62*i)),(25+(62*j)), 60, 60);
    	        	}
    	        }
            }else{
            	g2d.drawString("Problema para cargar el ambiente!", 10, 10);
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            doDrawing(g);
        }
    }
    	
    synchronized public int getRows() {
		return rows;
	}

    synchronized public int getColumns() {
		return columns;
	}

    synchronized public int getCountHoyos() {
		return hoyosposs.size();
	}
	
    synchronized public int getCountMosaicos() {
		return mosaicosposs.size();
	}

    synchronized public int increasePossX(int x){
		int temp = x;
		if( x == rows-1 )
			return 0;
		else
			return ++temp;
	}
	
    synchronized public int decreasePossX(int x){
		int temp = x;
		if( x == 0 )
			return rows-1;
		else
			return --temp;
	}
	
    synchronized public int increasePossY(int y){
		int temp = y;
		if( y == columns-1 )
			return 0;
		else
			return ++temp;
	}
	
    synchronized public int decreasePossY(int y){
		int temp = y;
		if( y == 0 )
			return columns-1;
		else
			return --temp;
	}
	
    synchronized public boolean isSpaceFree(int x,int y){
		char temp = map.charAt((x*(columns+1))+y);
		if( temp == '0' )
			return true;
		else
			return false;
	}
	
    synchronized public boolean isSpaceMosaico(int x,int y){
		char temp = map.charAt((x*(columns+1))+y);
		if( temp == '2')
			return true;
		else
			return false;
	}
	
    synchronized public boolean isSpaceHoyo(int x,int y){
		char temp = map.charAt((x*(columns+1))+y);
		if( temp == '1')
			return true;
		else
			return false;
	}
	
    synchronized public boolean isSpaceRobot(int x,int y){
		char temp = map.charAt((x*(columns+1))+y);
		if( temp == 'R')
			return true;
		else
			return false;
	}
	
    synchronized public char getValue(int x, int y){
		char temp = map.charAt((x*(columns+1))+y);
		return temp;
	}
	
    synchronized public void changeValPoint(int x, int y,char val){
		char [] temp = map.toCharArray();
		temp[(x*(columns+1))+y] = val;
		map = new String(temp);
	}
	
    synchronized public void changeValPoint(GridPoint point,char val){
		changeValPoint(point.getX(),point.getY(),val);
	}
	
    public void decreaseMosHoy(GridPoint mosc, GridPoint hoy){
		changeValPoint(mosc, 'R');
		changeValPoint(hoy, '0');
		removeMosaico(mosc);
		removeHoyo(hoy);
	}
	
    synchronized public void moveMosaico(GridPoint mosc,GridPoint newmosc){
		int i = 0;
		changeValPoint(mosc, 'R');
		changeValPoint(newmosc, '2');
		for(GridPoint itm : mosaicosposs){
			if( itm.isPointEqual(mosc)) break;
			++i;
		}
		mosaicosposs.remove(i);
		mosaicosposs.add(newmosc);
	}
	
	synchronized public void removeMosaico(GridPoint mosc){
		int i = 0;
		for(GridPoint itm : mosaicosposs){
			if( itm.isPointEqual(mosc)) break;
			++i;
		}
		mosaicosposs.remove(i);
	}
	
	synchronized public void removeHoyo(GridPoint hoy){
		int i = 0;
		for(GridPoint itm : hoyosposs){
			if( itm.isPointEqual(hoy)) break;
			++i;
		}
		hoyosposs.remove(i);
	}
	
	//regresa lista de mosaicos
	synchronized public LinkedList<GridPoint> getMosaicos(){
		return mosaicosposs;
	}
	
	//regresa lista de hoyos
	synchronized public LinkedList<GridPoint> getHoyos(){
		return hoyosposs;
	}
	
	//IMPRIME MAPA
	synchronized public String toString(){
		return "Tamo del grid: " + this.rows + "x" + this.columns + "\n" + "Number of hoyos: " + this.hoyosposs.size() + "\n" + "Numero of mosaicos: " + this.mosaicosposs.size() + " \nPunto inicial: " + this.iniposs.toString() + "\n" + this.map;
	}
    	
	synchronized public int getScore() {
		return 100 - (((getCountHoyos()*100)/numHoyosIni));
	}
	
	synchronized public GridPoint getIniposs() {
		return iniposs;
	}
	
	synchronized public boolean isMapLoaded() {
		return isMapLoaded;
	}

	/*
	 * TEST GRID
	 * */
    public static void main(String[] args) {
        GridMapEnviroment grid = new GridMapEnviroment();
        grid.getHoyos();
    }
}
