package TopologyHandler;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;

import NDMSMain.CdpDiscovery;
import NDMSMain.DiscoveryOperation;
import NDMSMain.LldpDiscovery;
import NDMSMain.frame1;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataSizeAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.layout.graph.BalloonTreeLayout;
import prefuse.activity.Activity;
import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.ImageFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.visual.VisualItem;

public class DrawTopology {

	static JFrame frame = null;
	private static final Schema DECORATOR_SCHEMA = PrefuseLib
			.getVisualItemSchema();
	static {
		DECORATOR_SCHEMA.setDefault(VisualItem.INTERACTIVE, false);
		DECORATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(128));
		DECORATOR_SCHEMA.setDefault(VisualItem.FONT,
				FontLib.getFont("Tahoma", 16));
	}

	public static void main() throws Exception {
		frame1 fra = new frame1();
		Graph graph = null;
		Timer times = new Timer();
		Timer times1 = new Timer();
		DiscoveryOperation dis = new DiscoveryOperation();
		LldpDiscovery ldp = new LldpDiscovery();
		CdpDiscovery cdp = new CdpDiscovery();
		ldp.lldpHostInt.clear();
		ldp.deviceModel.clear();
		ldp.lldpHostName.clear();
		ldp.lldpNeigInt.clear();
		ldp.lldpNeigMac.clear();
		ldp.lldpNeigName.clear();
		ldp.topologyNodeId.clear();

		cdp.cdpHostInt.clear();
		cdp.cdpDeviceModel.clear();
		cdp.cdpHostName.clear();
		cdp.cdpNeigInt.clear();
		cdp.cdpNeigName.clear();
		cdp.cdpTopologyNodeId.clear();

		try {

			graph = new GraphMLReader().readGraph("topology.xml");
		} catch (DataIOException e) {
			e.printStackTrace();
			System.err.println("Error loading graph. Exiting...");
			System.exit(1);

		}
		times.schedule(dis, 20000);
		ImageFactory imageFactory = new ImageFactory(100, 100);

		try {/*
			 * //load images and construct imageFactory. String images[] = new
			 * String[3]; images[0] = "data/images/Switch.jpg"; images[1] =
			 * "data/images/soup12212312.jpg"; images[2] =
			 * "data/images/Switch12312.png";
			 * 
			 * 
			 * String[] names = new String[] {"Switch","soup1","Router"};
			 * BufferedImage img = null;
			 * 
			 * 
			 * for(int i=0; i < images.length ; i++) { try { img =
			 * ImageIO.read(new File(images[i]));
			 * imageFactory.addImage(names[i],img);
			 * 
			 * } catch (IOException e){ } }
			 */
		} catch (Exception exp) {

		}

		Visualization vis = new Visualization();
		vis.add("graph", graph);

		LabelRenderer nodeRenderer = new LabelRenderer("name", "type");
		nodeRenderer.setVerticalAlignment(Constants.BOTTOM);
		nodeRenderer.setHorizontalPadding(0);
		nodeRenderer.setVerticalPadding(0);
		nodeRenderer.setImagePosition(Constants.TOP);
		nodeRenderer.setMaxImageDimensions(100, 100);
		nodeRenderer.setImageFactory(imageFactory);

		DefaultRendererFactory drf = new DefaultRendererFactory();
		drf.setDefaultRenderer(nodeRenderer);
		vis.setRendererFactory(drf);

		ColorAction nText = new ColorAction("graph.nodes", VisualItem.TEXTCOLOR);
		nText.setDefaultColor(ColorLib.gray(100));

		ColorAction nEdges = new ColorAction("graph.edges",
				VisualItem.STROKECOLOR, ColorLib.rgb(0, 200, 0));
		// nEdges.setDefaultColor(ColorLib.gray(100));

		// bundle the color actions
		ActionList draw = new ActionList();

		Control hoverc = new ControlAdapter() {

			public void popup(boolean fra)

			{

				JFrame popup = new JFrame("pooup");

				// popup.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				JLabel textLabel = new JLabel(
						"info:- device info /n HostName:-hostname- /n Firmware: -firmware-");

				textLabel.setPreferredSize(new Dimension(50, 100));

				popup.getContentPane().add(textLabel, BorderLayout.CENTER);

				PointerInfo a = MouseInfo.getPointerInfo();

				Point pt = a.getLocation();
				popup.setLocation(pt.x, pt.y);

				// popup.setLocation(xpos,ypos);

				if (fra == true) {
					popup.setVisible(true);
					System.out.println("popopoppopopopopo");
				} else {
					popup.dispose();
					System.out.println("falsefalse");
				}

			}

			public void itemEntered(VisualItem item, MouseEvent evt) {

				// JOptionPane.showMessageDialog(null, "test");
				item.setFillColor(item.getStrokeColor());
				item.setStrokeColor(ColorLib.rgb(0, 0, 0));
				item.getVisualization().repaint();
				popup(false);

			}

			public void itemExited(VisualItem item, MouseEvent evt) {

				item.setFillColor(item.getEndFillColor());
				item.setStrokeColor(item.getEndStrokeColor());
				item.getVisualization().repaint();
				// popup(false);
				System.out.println("exited");

			}

		};

		draw.add(new RepaintAction());

		// MAD - changing the size of the nodes dependent on the weight of the
		// people
		final DataSizeAction dsa = new DataSizeAction("graph.nodes", "size");
		draw.add(dsa);

		draw.add(nText);
		draw.add(new FontAction("graph.nodes", FontLib.getFont("Tahoma",
				Font.BOLD, 12)));
		draw.add(nEdges);

		vis.putAction("draw", draw);

		ActionList layout = new ActionList(Activity.DEFAULT_STEP_TIME);

		BalloonTreeLayout balloonlayout = new BalloonTreeLayout("graph", 50);
		layout.add(balloonlayout);

		Display d = new Display(vis);

		vis.putAction("layout", layout);

		d.addControlListener(new DragControl());
		d.addControlListener(hoverc);

		// pan with left-click drag on background
		// d.addControlListener(new PanControl());
		// zoom with right-click drag
		d.addControlListener(new ZoomControl());

		// -- 6. launch the visualization -------------------------------------

		// create a new window to hold the visualization

		if (frame == null) {
			frame = new JFrame("Network Discovery and Monitoring");
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setBounds(0, 0, screenSize.width, screenSize.height);
			frame.setVisible(true); // show the window
			frame.add(d);

		}

		// ensure application exits when window is closed
		frame.add(d);

		// Thread th = new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// frame.add(d);
		// System.out.println("*************************\n THREAD\n*************************");
		// frame.pack();
		// }
		// });
		// th.start();
		// th.sleep(10000);
		// layout components in window

		// times1.schedule(frame.add(d), 10000);

		// start up the animated layout
		System.out.println("Draw ustu");
		vis.run("draw");
		vis.run("layout");
		vis.run("hoverc");

	}

}
