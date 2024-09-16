import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class BeatBox {
	JFrame frame;
	JPanel panel;
	JButton button1;
	JButton button2;
	JButton button3;
	JButton button4;
	String[] instruments = {
			"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap",
			"High Tom","High Bongo", "Maracas", "Whistle", "Low Congo", "Cowbell", "Vibraslap", "Low-mid Tom",
			"High Agogo", "Open Hi Conga"
	};
	int[] instrumentsNumber = {
			35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63
	};
	ArrayList<JCheckBox> checkBoxList;
	Sequencer sequencer;
	Sequence sequence;
	Track track;
	
	public static void main(String[] args) {
		BeatBox start = new BeatBox();
		start.guiGo();
	} // end of main function
	public void guiGo( ) {
		frame = new JFrame("CyberBeatBoxVersion1.0");
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new JPanel(); // first panel on the right that holds all the button
		panel.setBackground(Color.white);
		JPanel mainPanel = new JPanel(); // second mainPanel
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBackground(Color.white);
		Font newFont = new Font("Sans Serif",Font.PLAIN, 16); // setting the font
		checkBoxList = new ArrayList<JCheckBox>();
		
		button1 = new JButton("Start");
		button1.addActionListener(new StartButtonActionListener());
		panel.add(button1);
		
		button2 = new JButton("Stop");
		button2.addActionListener(new StopButtonListener());
		panel.add(button2);
		
		button3 = new JButton("TempoUp");
		button3.addActionListener(new TempoUpListener());
		panel.add(button3);
		
		button4 = new JButton("TempoDown");
		button4.addActionListener(new TempoDownListener());
		panel.add(button4);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		mainPanel.add(BorderLayout.EAST,panel);
		
		JPanel checkPanel = new JPanel();
		checkPanel.setBackground(Color.white);
		checkPanel.setLayout(new GridLayout(16,16));
		// Lets add the checkboxes now
		for(int i=0;i<=255;i++) {
			JCheckBox checkBox = new JCheckBox();
			checkBox.setSelected(false);
			checkBoxList.add(checkBox);
			checkPanel.add(checkBox);
			
		}// end of for loop
		mainPanel.add(BorderLayout.CENTER, checkPanel);
		
		// now lets put the label 
		JPanel labelPanel = new JPanel();
		labelPanel.setBackground(Color.white);
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
		for(int i=0;i<instruments.length;i++) {
			JLabel label = new JLabel(instruments[i]);
			label.setFont(newFont);
			labelPanel.add(label);
		}
		mainPanel.add(BorderLayout.WEST, labelPanel);
		
		frame.getContentPane().add(mainPanel);
		frame.setVisible(true);
		frame.setSize(600,600);
		frame.pack();
		// now lets setup the musicSystem
		setUpMusicSystem();
		
	} // end of guiGo function
	public void setUpMusicSystem() {
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequence = new Sequence(Sequence.PPQ,4);
			track = sequence.createTrack();
			sequencer.setTempoInBPM(120);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public class StartButtonActionListener implements ActionListener {
		public void actionPerformed (ActionEvent  ev) {
			System.out.println("Start Button working");
			buildAndPlay(); // defining this later
		
		}
	} // end of StartButtonActionListener
	
	public class StopButtonListener implements ActionListener {
		public void actionPerformed (ActionEvent ev) {
			System.out.println("Stop Button working successfully");
			sequencer.stop();
		}
	} // end of StopButtonListener
	
	public class TempoUpListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			System.out.println("Temp Button Instated successfully");
			float tempo = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float) (tempo *1.20));
			}
	} // end of TempoUpListener
	
	public class TempoDownListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			System.out.println("Tempo down button created successfully");
			float tempo = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float) (tempo * 0.85)); 
		}
	} // end of TempoDownListener
	
	// lets create buildAndPlay now
	public void buildAndPlay() {
		int[] trackList = null;
		sequence.deleteTrack(track);
		track = sequence.createTrack();
		for(int i=0; i<16;i++) {
			int instrument = instrumentsNumber[i];
			trackList = new int[16]; // /we have to create a trackList for each and every instrument
			for(int j=0;j<16;j++) {
				if(checkBoxList.get(j + 16*i).isSelected()) {
					trackList[j] = instrument;
				}
				else
					trackList[j] = 0;
			} // closing the inner loop
			makeTrack(trackList); 
		} // closing the outer  loop
		
		try {
			sequencer.setSequence(sequence);
			sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
			sequencer.start();
			sequencer.setTempoInBPM(20);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	} // end of builAndPlay method
	public void makeTrack(int[] list) {
		for(int i=0;i<16;i++) {
			if(list[i]!=0) {
				track.add(makeEvent(144,9,list[i],100,i));
				track.add(makeEvent(176,1,127,0,i));
				track.add(makeEvent(128,9,list[i],100,i+1));
			}
		}
	} // end of makeTrack
	public MidiEvent makeEvent(int comd, int one , int two , int three, int four) {
		MidiEvent event = null;
		try {
			ShortMessage msg = new ShortMessage();
			msg.setMessage(comd,one,two,three);
			event = new MidiEvent(msg,four);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return event;
	} // end of makeEvent
	
	
} // end of public class