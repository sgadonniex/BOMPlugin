
package com.eteks.test;
import com.eteks.sweethome3d.plugin.PluginAction; 
import javax.swing.JOptionPane; 
import javax.swing.JTextField; 
import javax.swing.JPanel; 
import javax.swing.JLabel; 
import javax.swing.BoxLayout;




public class BOMSettings extends PluginAction { private BOMPlugin plugin;

public BOMSettings(BOMPlugin plugin) {
    if (plugin == null) {
        throw new IllegalArgumentException("BOMPlugin cannot be null");
    }
    this.plugin = plugin;
    putPropertyValue(Property.NAME, "Materials Settings");
    putPropertyValue(Property.MENU, "Bill Of Materials");
    setEnabled(true);
}

@Override
public void execute() {
	Brick brick = plugin.getBrick();
	Brick halfbrick = plugin.getHalfBrick();
	Brick channelbrick = plugin.getChannelBrick();
	Brick halfchannelbrick = plugin.getHalfChannelBrick();
	

    JTextField brickLengthField = new JTextField(String.valueOf(brick.getLength()));
    JTextField brickWidthField = new JTextField(String.valueOf(brick.getWidth()));
    JTextField brickHeightField = new JTextField(String.valueOf(brick.getHeight()));
    
    JTextField halfBrickLengthField = new JTextField(String.valueOf(halfbrick.getLength()));
    JTextField halfBrickWidthField = new JTextField(String.valueOf(halfbrick.getWidth()));
    JTextField halfBrickHeightField = new JTextField(String.valueOf(halfbrick.getHeight()));
    
    JTextField channelBrickLengthField = new JTextField(String.valueOf(channelbrick.getLength()));
    JTextField channelBrickWidthField = new JTextField(String.valueOf(channelbrick.getWidth()));
    JTextField channelBrickHeightField = new JTextField(String.valueOf(channelbrick.getHeight()));
    
    JTextField halfChannelBrickLengthField = new JTextField(String.valueOf(halfchannelbrick.getLength()));
    JTextField halfChannelBrickWidthField = new JTextField(String.valueOf(halfchannelbrick.getWidth()));
    JTextField halfChannelBrickHeightField = new JTextField(String.valueOf(halfchannelbrick.getHeight()));
    
    JTextField subwallHeightField = new JTextField(String.valueOf(plugin.getSubwallHeight()));
    JTextField rebarLengthField = new JTextField(String.valueOf(plugin.getRebarLength()));
    
    JTextField outputFileNameField = new JTextField(plugin.getOutputFileName());
    
    JPanel panel = new JPanel();
  
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(new JLabel("Brick Length in cm:"));
    panel.add(brickLengthField);
    panel.add(new JLabel("Brick Width  in cm:"));
    panel.add(brickWidthField);
    panel.add(new JLabel("Brick Height in cm:"));
    panel.add(brickHeightField);
    
    panel.add(new JLabel("Half Brick Length in cm:"));
    panel.add(halfBrickLengthField);
    panel.add(new JLabel("Half Brick Width in cm:"));
    panel.add(halfBrickWidthField);
    panel.add(new JLabel("Half Brick Height in cm:"));
    panel.add(halfBrickHeightField);
    
    panel.add(new JLabel("Channel Brick Length in cm:"));
    panel.add(channelBrickLengthField);
    panel.add(new JLabel("Channel Brick Width in cm:"));
    panel.add(channelBrickWidthField);
    panel.add(new JLabel("Channel Brick Height in cm:"));
    panel.add(channelBrickHeightField);
    
    panel.add(new JLabel("Half Channel Brick Length in cm:"));
    panel.add(halfChannelBrickLengthField);
    panel.add(new JLabel("Half Channel Brick Width in cm:"));
    panel.add(halfChannelBrickWidthField);
    panel.add(new JLabel("Half Channel Brick Height in cm:"));
    panel.add(halfChannelBrickHeightField);
    
    panel.add(new JLabel("Subwall Height (used to calculate channel bricks) in cm:"));
    panel.add(subwallHeightField);
    
    panel.add(new JLabel("Rebar Length in cm:"));
    panel.add(rebarLengthField);
    
    panel.add(new JLabel("Output File Name:"));
    panel.add(outputFileNameField); 

    int result = JOptionPane.showConfirmDialog(null, panel, "BOM Settings", JOptionPane.OK_CANCEL_OPTION);
    
    if (result == JOptionPane.OK_OPTION) {
        brick.setLength(Float.parseFloat(brickLengthField.getText()));
        brick.setWidth(Float.parseFloat(brickWidthField.getText()));
        brick.setHeight(Float.parseFloat(brickHeightField.getText()));
        halfbrick.setLength(Float.parseFloat(halfBrickLengthField.getText()));
        halfbrick.setWidth(Float.parseFloat(halfBrickWidthField.getText()));
        halfbrick.setHeight(Float.parseFloat(halfBrickHeightField.getText()));
        channelbrick.setLength(Float.parseFloat(channelBrickLengthField.getText()));
        channelbrick.setWidth(Float.parseFloat(channelBrickWidthField.getText()));
        channelbrick.setHeight(Float.parseFloat(channelBrickHeightField.getText()));
        halfchannelbrick.setLength(Float.parseFloat(halfChannelBrickLengthField.getText()));
        halfchannelbrick.setWidth(Float.parseFloat(halfChannelBrickWidthField.getText()));
        halfchannelbrick.setHeight(Float.parseFloat(halfChannelBrickHeightField.getText()));
        plugin.setSubwallHeight(Float.parseFloat(subwallHeightField.getText()));
        plugin.setRebarLength(Float.parseFloat(rebarLengthField.getText()));
        plugin.setOutputFileName(outputFileNameField.getText());
        
    }
}

} 