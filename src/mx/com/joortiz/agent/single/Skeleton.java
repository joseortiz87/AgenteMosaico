package mx.com.joortiz.agent.single;
import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class Surface extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	EnvMap ambiente;
	
	Surface(EnvMap ambiente){
		this.ambiente = ambiente;
	}

    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if( ambiente != null ){
	        g2d.drawString("Numero de Hoyos que restan: " + ambiente.hoyos, 10, 10);
	        g2d.setColor(new Color(102, 102, 153));
	        for(int i=0;i<ambiente.rows;++i){
	        	for(int j=0;j<ambiente.columns;++j){
	        		if( ambiente.isSpaceHoyo(j, i) ){
	        	        g2d.setColor(new Color(0, 0, 0));
	        		}else{
	        			if( ambiente.isSpaceMosaico(j, i) ){
	        				g2d.setColor(new Color(220,20,60));
	        			}else{
	        				if( ambiente.isSpaceRobot(j, i) ){  //ROBOT
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

public class Skeleton extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	EnvMap ambiente;
	Surface surface;
	
    public Skeleton(EnvMap ambiente) {
    	this.ambiente = ambiente;
    	this.surface = new Surface(this.ambiente);
        initUI();
    }

    private void initUI() {

        setTitle("Tarea 1 Problema Mosaicos Hoyos");

        add(this.surface);

        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    public void repaintMap(){
    	this.surface.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                EnvMap map = new EnvMap("Map.txt");
                Skeleton sk = new Skeleton(map);
                sk.setVisible(true);
            }
        });
    }
}