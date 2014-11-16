/*
 *  Copyright 2009,2010 Martin Roth (mhroth@gmail.com)
 * 
 *  This file is part of JAsioHost.
 *
 *  JAsioHost is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JAsioHost is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with JAsioHost.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.paranoidfrog.ui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.synthbot.jasiohost.AsioChannel;
import com.synthbot.jasiohost.AsioDriver;
import com.synthbot.jasiohost.AsioDriverListener;
import com.synthbot.jasiohost.AsioDriverState;

/**
 * The <code>ExampleHost</code> demonstrates how to use an
 * <code>AsioDriver</code> in order to read and write audio from a loaded ASIO
 * driver. A small GUI is presented, allowing the user to select any of the
 * available ASIO drivers on the system. The <i>Start</i> button loads the
 * driver and plays a 440Hz tone. The <i>Stop</i> button stops this process and
 * unloads the driver. The <i>Control Panel</i> button opens the driver's
 * control panel for any additional configuration.
 */
public class AudioEngine extends JFrame implements AsioDriverListener {

	private static final long serialVersionUID = 1L;

	private AsioDriver asioDriver;
	private Set<AsioChannel> activeChannels;
	private int sampleIndex;
	private int bufferSize;
	private double sampleRate;
	private float[] output;
	private int numInputs;
	private int numOutputs;
	private JComboBox comboBox;
	private AsioDriverListener host;

	public AudioEngine() {
		super("Paranoid Frog");

		activeChannels = new HashSet<AsioChannel>();

		comboBox = new JComboBox(AsioDriver.getDriverNames().toArray());
		comboBox.setBackground(Color.WHITE);
		final JButton buttonStart = new JButton("Start!");
		final JButton buttonStop = new JButton("Stop!");
		final JButton buttonControlPanel = new JButton("Control Panel!");

		buttonStart.setBackground(new Color(52, 129, 253));
		buttonStop.setBackground(Color.BLACK);
		buttonControlPanel.setBackground(Color.WHITE);

		this.setForeground(Color.GREEN);
		host = this;

		buttonStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				initDrivers();
			}
		});

		buttonStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				stopDrivers();
			}
		});

		buttonControlPanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				openControlPanel();
			}
		});

		this.setLayout(new GridLayout(2, 2));
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		this.add(comboBox);
		panel.add(buttonStart);
		panel.add(buttonStop);
		panel.add(buttonControlPanel);
		this.add(panel);
		this.setBackground(Color.WHITE);
		this.getContentPane().setBackground(Color.WHITE);

		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				if (asioDriver != null) {
					asioDriver.shutdownAndUnloadDriver();
				}
			}
		});

		this.setSize(600, 600);

		this.setResizable(false);
//		this.setVisible(true);
	}

	public void openControlPanel() {
		if (asioDriver != null
				&& asioDriver.getCurrentState().ordinal() >= AsioDriverState.INITIALIZED.ordinal()) {
			asioDriver.openControlPanel();
		}
	}

	public void stopDrivers() {
		if (asioDriver != null) {
			asioDriver.shutdownAndUnloadDriver();
			activeChannels.clear();
			asioDriver = null;
		}
	}

	public void initDrivers() {
		if (asioDriver == null) {
			asioDriver = AsioDriver.getDriver(comboBox.getSelectedItem().toString());
			asioDriver.addAsioDriverListener(host);
			numInputs = asioDriver.getNumChannelsInput();
			numOutputs = asioDriver.getNumChannelsOutput();
			for (int i = 0; i < numInputs; i++) {
				activeChannels.add(asioDriver.getChannelInput(i));
			}
			for (int j = 0; j < numOutputs; j++) {
				activeChannels.add(asioDriver.getChannelOutput(j));
			}
			sampleIndex = 0;
			bufferSize = asioDriver.getBufferPreferredSize();
			System.out.println(bufferSize);
			sampleRate = asioDriver.getSampleRate();
			output = new float[bufferSize];
			asioDriver.createBuffers(activeChannels);
			asioDriver.start();
		}
	}

	public void bufferSwitch(long systemTime, long samplePosition, Set<AsioChannel> channels) {
		float[] outputLeftArray = new float[bufferSize];
		float[] outputRightArray = new float[bufferSize];
		for (AsioChannel activeChannel : activeChannels) {
			if (activeChannel.isInput()) {
				
				double treshHold = 50.0f; 
				double multiplier = 1.0/0x7fff; // normalize input to double -1,1
				double wetSample = 0.0f;
			     for(int i=0;i<bufferSize;i++){
			    	 double inputSample = multiplier*activeChannel.getByteBuffer().getInt();
			    	 double absolutInputSample = java.lang.Math.abs(inputSample);
//			    	 System.out.println(absolutInputSample);
			        if(absolutInputSample<treshHold){
//			        	System.out.println("Meh.");
			            wetSample=(outputRightArray[i]*2*(multiplier));
			        }
			        else if(absolutInputSample<2*treshHold){
			            if(inputSample>0){
//			            	System.out.println("+");
			            	wetSample = (3-(2-inputSample*3)*(2-inputSample*3))/3;
			            }
			            else {
			            	if(inputSample<0){
//			            		System.out.println("-");
			            		wetSample=-(3-(2-absolutInputSample*3)*(2-absolutInputSample*3))/3;
			            	}
				            	
			            }	
			        }
			        else if(absolutInputSample>=2*treshHold){
			            if(inputSample>0)wetSample=1;
			            else if(inputSample<0)wetSample=-1;
			        }
			        outputRightArray[i] = (float)(wetSample/multiplier);
			    }
			}
		}

//		for (AsioChannel activeChannel : channels) {
//			if (activeChannel.isInput()) {
//				for (int i = 0; i < bufferSize; i++) {
//					outputLeftArray[i] += ((float) activeChannel
//							.getByteBuffer().getInt()) / Integer.MAX_VALUE;
//					outputRightArray[i] += ((float) activeChannel
//							.getByteBuffer().getInt()) / Integer.MAX_VALUE;
//				}
//			}
//		}

		// We shall do a separate loop of the channels as there is no guarantee
		// that all the input

		// channels will be returned before the outputs.

		boolean sideSwitch = false;
		for (AsioChannel activeChannel : channels) {
			if (!activeChannel.isInput()) {
				if (sideSwitch)
					activeChannel.write(outputRightArray);
				else
					activeChannel.write(outputRightArray);
				sideSwitch = !sideSwitch;
			}
		}
	}

	public void bufferSizeChanged(int bufferSize) {
		System.out.println("bufferSizeChanged() callback received.");
	}

	public void latenciesChanged(int inputLatency, int outputLatency) {
		System.out.println("latenciesChanged() callback received.");
	}

	public void resetRequest() {
		/*
		 * This thread will attempt to shut down the ASIO driver. However, it
		 * will block on the AsioDriver object at least until the current method
		 * has returned.
		 */
		new Thread() {
			@Override
			public void run() {
				System.out
						.println("resetRequest() callback received. Returning driver to INITIALIZED state.");
				asioDriver.returnToState(AsioDriverState.INITIALIZED);
			}
		}.start();
	}

	public void resyncRequest() {
		System.out.println("resyncRequest() callback received.");
	}

	public void sampleRateDidChange(double sampleRate) {
		System.out.println("sampleRateDidChange() callback received.");
	}

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		AudioEngine host = new AudioEngine();
	}

}