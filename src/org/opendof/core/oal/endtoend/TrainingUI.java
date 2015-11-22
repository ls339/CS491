package org.opendof.core.oal.endtoend;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import org.opendof.core.oal.endtoend.DOFAbstraction;


@SuppressWarnings("serial")
public class TrainingUI extends JFrame {

    JPanel pnlRequestor;
    
    //Set visual elements
    JPanel pnlSet;
    
    JRadioButton btnProvider1;
    JRadioButton btnProvider2;
    ButtonGroup providerListButtonGroup;
    JPanel pnlProviderSelection;
    
    JRadioButton btnSetTrue;
    JRadioButton btnSetFalse;
    ButtonGroup setButtonGroup;
    JLabel lblSetResults;
    JButton btnSetOp;
    
    //get visual elements
    JPanel pnlGet;
    JButton btnGetOp;
    JLabel lblGetResults;
    
    // ETE beginSession visual elements
    JPanel pnlBeginSession;
    JButton btnBeginSession;
    JLabel lblSessionResults;
    
    // ETE ETESession visual elements
    JPanel pnlETESession;
    JButton btnETESession;
    JLabel lblETESession;
    
    //invoke visual elements
    JPanel pnlInvoke;
    JLabel lblCurrentTime;
    JButton btnInvokeOp;
    JLabel lblInvokeResults;

    JPanel pnlAsnychRequestor;
    
    //Set visual elements
    JPanel pnlBeginSet;
    JRadioButton btnBeginSetTrue;
    JRadioButton btnBeginSetFalse;
    ButtonGroup setBeginButtonGroup;
    JLabel lblBeginSetResults;
    JLabel lblBeginSetResults2;
    JButton btnBeginSetOp;
    
    //get visual elements
    JPanel pnlBeginGet;
    JButton btnBeginGetOp;
    JLabel lblBeginGetResults;
    JLabel lblBeginGetResults2;
    
    //invoke visual elements
    JPanel pnlBeginInvoke;
    JLabel lblBeginCurrentTime;
    JButton btnBeginInvokeOp;
    JLabel lblBeginInvokeResults;
    JLabel lblBeginInvokeResults2;
    
    //provider visual elements
    JPanel pnlProviderPanel;
    JRadioButton btnProviderTrue;
    JRadioButton btnProviderFalse;
    ButtonGroup providerPropertyButtonGroup;
    JLabel lblLastProviderOperation;
    JLabel lblLastReceivedTime;

    //provider visual elements
    JPanel pnlProvider2Panel;
    JRadioButton btnProvider2True;
    JRadioButton btnProvider2False;
    ButtonGroup providerProperty2ButtonGroup;
    JLabel lblLastProvider2Operation;
    JLabel lblLastReceivedTime2;
    
    
    
    ActionListener actionListener = new TrainingUIActionListener();
    java.util.Timer timeUpdater = new java.util.Timer();
    Date now = new Date();
    
    DOFAbstraction dofAbstraction;
    Provider provider;
    Provider provider2;
    Requestor requestor;
    
    String providerOID = "[3:prov@opendof.org]";
    String provider2OID = "[3:prov2@opendof.org]";
    
    public TrainingUI(){
        initDOF();
        initUI();
    }
    
    private void initDOF(){
        dofAbstraction = new DOFAbstraction();
        provider = new Provider(dofAbstraction.createSystem("provider"), providerOID);
        provider2 = new Provider(dofAbstraction.createSystem("provider2"), provider2OID);
        provider2.setDelay(0);
        requestor = new Requestor(dofAbstraction.createSystem("requestor"), this);  
    }
    
    private void initUI(){
        this.addWindowListener(new WindowCloseHandler());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        Font defaultFont = new Font("Microsoft Sans Serif", Font.PLAIN, 11);
        
        pnlRequestor = new JPanel();
        pnlRequestor.setBounds(23, 10, 250, 500);
        TitledBorder borderRequestor = BorderFactory.createTitledBorder("+Session Requestor+");
        borderRequestor.setTitleFont(defaultFont);
        pnlRequestor.setBorder(borderRequestor);
        pnlRequestor.setLayout(null);
        
        pnlProviderSelection = new JPanel();
        pnlProviderSelection.setBounds(17, 20, 210, 60);
        pnlProviderSelection.setLayout(null);
        
        btnProvider1 = new JRadioButton("prov@opendof.org");
        btnProvider1.addActionListener(actionListener);
        btnProvider1.setFont(defaultFont);
        btnProvider1.setBounds(17, 10, 180, 20);
        
        btnProvider2 = new JRadioButton("prov2@opendof.org");
        btnProvider2.addActionListener(actionListener);
        btnProvider2.setFont(defaultFont);
        btnProvider2.setBounds(17, 35, 180, 20);
        
        pnlProviderSelection.add(btnProvider1);
        pnlProviderSelection.add(btnProvider2);
        
        providerListButtonGroup = new ButtonGroup();
        providerListButtonGroup.add(btnProvider1);
        providerListButtonGroup.add(btnProvider2);
        btnProvider2.setSelected(true);
        
        //get visual elements
        pnlGet = new JPanel();
        pnlGet.setBounds(17, 80, 210, 95);
        TitledBorder borderGetOp = BorderFactory.createTitledBorder("Get operation");
        //TitledBorder borderBeginSession = BorderFactory.createTitledBorder("Begin Session");
        //borderGetOp.setTitleFont(defaultFont);
        //borderBeginSession.setTitleFont(defaultFont);
        pnlGet.setBorder(borderGetOp);
        //pnlGet.setBorder(borderBeginSession);
        pnlGet.setLayout(null);
        
        // ETE Session visual elements
        pnlBeginSession = new JPanel();
        //pnlBeginSession.setBounds(17, 80, 210, 60);
        pnlBeginSession.setBounds(17, 80, 210, 95);
        TitledBorder borderBeginSession = BorderFactory.createTitledBorder("Begin Session");
        borderBeginSession.setTitleFont(defaultFont);
        pnlBeginSession.setBorder(borderBeginSession);
        pnlBeginSession.setLayout(null);
        
        // ETE ETESession visual elements
        pnlETESession = new JPanel();
        pnlETESession.setBounds(17, 250, 210, 100);
        TitledBorder borderETESession = BorderFactory.createTitledBorder("end-to-end Session");
        borderETESession.setTitleFont(defaultFont);
        pnlETESession.setBorder(borderETESession);
        pnlETESession.setLayout(null);
        
        btnGetOp = new JButton("<html><center>MyRequestor.Get()<br>&lt;--&nbsp;&lt;--</center></html>");
        btnGetOp.addActionListener(actionListener);
        btnGetOp.setFont(defaultFont);
        btnGetOp.setMargin(new Insets(0, 0, 0, 0));
        btnGetOp.setIconTextGap(0);
        btnGetOp.setVerticalTextPosition(AbstractButton.CENTER);
        btnGetOp.setHorizontalTextPosition(AbstractButton.CENTER);
        btnGetOp.setBounds(20, 20, 140, 35);
        
        // ETE
        btnBeginSession = new JButton("<html><center>beginSession()<br>&lt;--&nbsp;&lt;--</center></html>");
        btnBeginSession.addActionListener(actionListener);
        btnBeginSession.setFont(defaultFont);
        btnBeginSession.setMargin(new Insets(0, 0, 0, 0));
        btnBeginSession.setIconTextGap(0);
        btnBeginSession.setVerticalTextPosition(AbstractButton.CENTER);
        btnBeginSession.setHorizontalTextPosition(AbstractButton.CENTER);
        btnBeginSession.setBounds(20, 20, 140, 35);       
        
        // ETE
        btnETESession = new JButton("<html><center>end-to-end Session()<br>&lt;--&nbsp;&lt;--</center></html>");
        btnETESession.addActionListener(actionListener);
        btnETESession.setFont(defaultFont);
        btnETESession.setMargin(new Insets(0, 0, 0, 0));
        btnETESession.setIconTextGap(0);
        btnETESession.setVerticalTextPosition(AbstractButton.CENTER);
        btnETESession.setHorizontalTextPosition(AbstractButton.CENTER);
        btnETESession.setBounds(20, 20, 140, 35);    
        
        lblGetResults = new JLabel("Last retieved value:  Undefined");
        lblGetResults.setFont(defaultFont);
        lblGetResults.setBounds(20, 60, 170, 30);
        
        lblSessionResults = new JLabel("Session Enabled:  False");
        lblSessionResults.setFont(defaultFont);
        lblSessionResults.setBounds(20, 60, 170, 30);   
        
        lblETESession = new JLabel("end-to-end Session Enabled:  False");
        lblETESession.setFont(defaultFont);
        lblETESession.setBounds(20, 60, 170, 30);   
        
        //pnlGet.add(btnGetOp);
        //pnlGet.add(lblGetResults);
        //pnlGet.add(btnBeginSession);
        //pnlGet.add(lblSessionResults);
        pnlBeginSession.add(btnBeginSession);
        pnlBeginSession.add(lblSessionResults);
        pnlETESession.add(btnETESession);
        pnlETESession.add(lblETESession);
        
        //Set visual elements
        pnlSet = new JPanel();
        pnlSet.setBounds(17, 180, 210, 135);
        TitledBorder borderSetOp = BorderFactory.createTitledBorder("Set operation");
        borderSetOp.setTitleFont(defaultFont);
        pnlSet.setBorder(borderSetOp);
        pnlSet.setLayout(null);
                
        btnSetTrue = new JRadioButton("True");
        btnSetTrue.addActionListener(actionListener);
        btnSetTrue.setFont(defaultFont);
        btnSetTrue.setBounds(20, 20, 100, 20);
        
        btnSetFalse = new JRadioButton("False");
        btnSetFalse.addActionListener(actionListener);
        btnSetFalse.setFont(defaultFont);
        btnSetFalse.setBounds(20, 45, 100, 20);
        
        setButtonGroup = new ButtonGroup();
        setButtonGroup.add(btnSetTrue);
        setButtonGroup.add(btnSetFalse);
        
        btnSetOp = new JButton("<html><center>MyRequestor.Set()<br>&raquo;&nbsp;&raquo;</center></html>");
        btnSetOp.setEnabled(false);
        btnSetOp.addActionListener(actionListener);
        btnSetOp.setFont(defaultFont);
        btnSetOp.setBounds(20, 70, 140, 35);
        
        lblSetResults = new JLabel();
        lblSetResults.setText("Last Set operation:  Undefined");
        lblSetResults.setFont(defaultFont);
        lblSetResults.setBounds(20, 110, 170, 20);
        
        pnlSet.add(btnSetTrue);
        pnlSet.add(btnSetFalse);
        pnlSet.add(btnSetOp);
        pnlSet.add(lblSetResults);
        
        //invoke visual elements
        pnlInvoke = new JPanel();
        pnlInvoke.setBounds(15, 320, 210, 160);
        TitledBorder borderInvokeOp = BorderFactory.createTitledBorder("Invoke operation");
        borderInvokeOp.setTitleFont(defaultFont);
        pnlInvoke.setBorder(borderInvokeOp);
        pnlInvoke.setLayout(null);
                
        lblCurrentTime = new JLabel(DateFormat.getTimeInstance().format(new Date()));
        lblCurrentTime.setBounds(20, 20, 150, 30);
        lblCurrentTime.setFont(defaultFont);
        
        btnInvokeOp = new JButton("<html><center>&raquo;&nbsp;&raquo;<br>MyRequestor.Invoke()<br>&laquo;&nbsp;&laquo;</center></html>");
        btnInvokeOp.addActionListener(actionListener);
        btnInvokeOp.setFont(defaultFont);
        btnInvokeOp.setBounds(20, 55, 140, 50);
        
        lblInvokeResults = new JLabel("Last returned value:  Undefined");
        lblInvokeResults.setFont(defaultFont);
        lblInvokeResults.setBounds(20, 110, 170, 30);
        
        pnlInvoke.add(lblCurrentTime);
        pnlInvoke.add(btnInvokeOp);
        pnlInvoke.add(lblInvokeResults);
        
        //Asynch visual elements
        pnlAsnychRequestor = new JPanel();
        pnlAsnychRequestor.setBounds(278, 10, 270, 500);
        TitledBorder borderAsynchRequestor = BorderFactory.createTitledBorder("+Asynchronous Requestor+");
        borderAsynchRequestor.setTitleFont(defaultFont);
        pnlAsnychRequestor.setBorder(borderAsynchRequestor);
        pnlAsnychRequestor.setLayout(null);
        
        //Begin get visual elements        
        pnlBeginGet = new JPanel();
        pnlBeginGet.setBounds(17, 20, 230, 105);
        TitledBorder borderBeginGetOp = BorderFactory.createTitledBorder("Begin Get operation");
        borderBeginGetOp.setTitleFont(defaultFont);
        pnlBeginGet.setBorder(borderBeginGetOp);
        pnlBeginGet.setLayout(null);
        
        btnBeginGetOp = new JButton("<html><center>MyRequestor.BeginGet()<br>&lt;--&nbsp;&lt;--</center></html>");
        btnBeginGetOp.addActionListener(actionListener);
        btnBeginGetOp.setFont(defaultFont);
        btnBeginGetOp.setMargin(new Insets(0, 0, 0, 0));
        btnBeginGetOp.setIconTextGap(0);
        btnBeginGetOp.setVerticalTextPosition(AbstractButton.CENTER);
        btnBeginGetOp.setHorizontalTextPosition(AbstractButton.CENTER);
        btnBeginGetOp.setBounds(20, 20, 150, 35);
        btnBeginGetOp.setEnabled(false); // Disable button
        
        lblBeginGetResults = new JLabel("Provider 1 Last retieved value:  Undefined");
        lblBeginGetResults.setFont(defaultFont);
        lblBeginGetResults.setBounds(20, 60, 200, 20);

        lblBeginGetResults2 = new JLabel("Provider 2 Last retieved value:  Undefined");
        lblBeginGetResults2.setFont(defaultFont);
        lblBeginGetResults2.setBounds(20, 80, 200, 20);
        
        pnlBeginGet.add(btnBeginGetOp);
        pnlBeginGet.add(lblBeginGetResults);
        pnlBeginGet.add(lblBeginGetResults2);
        
        //Set visual elements
        pnlBeginSet = new JPanel();
        pnlBeginSet.setBounds(17, 135, 230, 155);
        TitledBorder borderBeginSetOp = BorderFactory.createTitledBorder("Begin Set operation");
        borderBeginSetOp.setTitleFont(defaultFont);
        pnlBeginSet.setBorder(borderBeginSetOp);
        pnlBeginSet.setLayout(null);
        
        btnBeginSetTrue = new JRadioButton("True");
        btnBeginSetTrue.addActionListener(actionListener);
        btnBeginSetTrue.setFont(defaultFont);
        btnBeginSetTrue.setBounds(20, 20, 100, 20);
        
        btnBeginSetFalse = new JRadioButton("False");
        btnBeginSetFalse.addActionListener(actionListener);
        btnBeginSetFalse.setFont(defaultFont);
        btnBeginSetFalse.setBounds(20, 45, 100, 20);
        
        setBeginButtonGroup = new ButtonGroup();
        setBeginButtonGroup.add(btnBeginSetTrue);
        setBeginButtonGroup.add(btnBeginSetFalse);
        
        btnBeginSetOp = new JButton("<html><center>MyRequestor.BeginSet()<br>&raquo;&nbsp;&raquo;</center></html>");
        btnBeginSetOp.setEnabled(false);
        btnBeginSetOp.addActionListener(actionListener);
        btnBeginSetOp.setFont(defaultFont);
        btnBeginSetOp.setMargin(new Insets(0, 0, 0, 0));
        btnBeginSetOp.setBounds(20, 70, 150, 35);
        
        lblBeginSetResults = new JLabel();
        lblBeginSetResults.setText("Provider 1 Last Set operation:  Undefined");
        lblBeginSetResults.setFont(defaultFont);
        lblBeginSetResults.setBounds(20, 110, 200, 20);
        
        lblBeginSetResults2 = new JLabel();
        lblBeginSetResults2.setText("Provider 2 Last Set operation:  Undefined");
        lblBeginSetResults2.setFont(defaultFont);
        lblBeginSetResults2.setBounds(20, 130, 200, 20);
        
        pnlBeginSet.add(btnBeginSetTrue);
        pnlBeginSet.add(btnBeginSetFalse);
        pnlBeginSet.add(btnBeginSetOp);
        pnlBeginSet.add(lblBeginSetResults);
        pnlBeginSet.add(lblBeginSetResults2);
                
        //invoke visual elements
        pnlBeginInvoke = new JPanel();
        pnlBeginInvoke.setBounds(15, 300, 230, 160);
        TitledBorder borderBeginInvokeOp = BorderFactory.createTitledBorder("Begin Invoke operation");
        borderBeginInvokeOp.setTitleFont(defaultFont);
        pnlBeginInvoke.setBorder(borderBeginInvokeOp);
        pnlBeginInvoke.setLayout(null);

        lblBeginCurrentTime = new JLabel(DateFormat.getTimeInstance().format(new Date()));
        lblBeginCurrentTime.setBounds(20, 20, 150, 30);
        lblBeginCurrentTime.setFont(defaultFont);
        
        btnBeginInvokeOp = new JButton("<html><center>&raquo;&nbsp;&raquo;<br>MyRequestor.BeginInvoke()<br>&laquo;&nbsp;&laquo;</center></html>");
        btnBeginInvokeOp.addActionListener(actionListener);
        btnBeginInvokeOp.setFont(defaultFont);
        btnBeginInvokeOp.setMargin(new Insets(0, 0, 0, 0));
        btnBeginInvokeOp.setBounds(20, 55, 150, 50);
        
        lblBeginInvokeResults = new JLabel("Provider 1 Last returned value:  Undefined");
        lblBeginInvokeResults.setFont(defaultFont);
        lblBeginInvokeResults.setBounds(20, 110, 200, 20);
        
        lblBeginInvokeResults2 = new JLabel("Provider 2 Last returned value:  Undefined");
        lblBeginInvokeResults2.setFont(defaultFont);
        lblBeginInvokeResults2.setBounds(20, 130, 200, 20);

        pnlBeginInvoke.add(lblBeginCurrentTime);
        pnlBeginInvoke.add(btnBeginInvokeOp);
        pnlBeginInvoke.add(lblBeginInvokeResults);
        pnlBeginInvoke.add(lblBeginInvokeResults2);
        
        //provider 1 visual elements
        pnlProviderPanel = new JPanel();
        pnlProviderPanel.setBounds(553, 10, 170, 170);
        TitledBorder providerBorder = BorderFactory.createTitledBorder("Provider");
        providerBorder.setTitleFont(defaultFont);
        pnlProviderPanel.setBorder(providerBorder);
        pnlProviderPanel.setLayout(null);
        
        btnProviderTrue = new JRadioButton("True");
        btnProviderTrue.addActionListener(actionListener);
        btnProviderTrue.setFont(defaultFont);
        btnProviderTrue.setBounds(15, 15, 100, 20);
        
        btnProviderFalse = new JRadioButton("False");
        btnProviderFalse.addActionListener(actionListener);
        btnProviderFalse.setSelected(true);
        btnProviderFalse.setFont(defaultFont);
        btnProviderFalse.setBounds(15, 40, 100, 20);
        
        providerPropertyButtonGroup = new ButtonGroup();
        providerPropertyButtonGroup.add(btnProviderTrue);
        providerPropertyButtonGroup.add(btnProviderFalse);
        
        lblLastProviderOperation = new JLabel("<html>Last Operation:<br>Undefined<html>");
        lblLastProviderOperation.setFont(defaultFont);
        lblLastProviderOperation.setBounds(15, 80, 170, 30);

        lblLastReceivedTime = new JLabel("<html>Last Received Time:<br>Undefined<html>");
        lblLastReceivedTime.setFont(defaultFont);
        lblLastReceivedTime.setBounds(15, 130, 170, 30);
        
        pnlProviderPanel.add(btnProviderTrue);
        pnlProviderPanel.add(btnProviderFalse);
        pnlProviderPanel.add(lblLastProviderOperation);
        pnlProviderPanel.add(lblLastReceivedTime);
        
      //provider2 visual elements
        pnlProvider2Panel = new JPanel();
        pnlProvider2Panel.setBounds(553, 190, 170, 170);
        TitledBorder provider2Border = BorderFactory.createTitledBorder("Provider");
        provider2Border.setTitleFont(defaultFont);
        pnlProvider2Panel.setBorder(provider2Border);
        pnlProvider2Panel.setLayout(null);
        
        btnProvider2True = new JRadioButton("True");
        btnProvider2True.addActionListener(actionListener);
        btnProvider2True.setFont(defaultFont);
        btnProvider2True.setBounds(15, 15, 100, 20);
        
        btnProvider2False = new JRadioButton("False");
        btnProvider2False.addActionListener(actionListener);
        btnProvider2False.setSelected(true);
        btnProvider2False.setFont(defaultFont);
        btnProvider2False.setBounds(15, 40, 100, 20);
        
        providerProperty2ButtonGroup = new ButtonGroup();
        providerProperty2ButtonGroup.add(btnProvider2True);
        providerProperty2ButtonGroup.add(btnProvider2False);
        
        lblLastProvider2Operation = new JLabel("<html>Last Operation:<br>Undefined<html>");
        lblLastProvider2Operation.setFont(defaultFont);
        lblLastProvider2Operation.setBounds(15, 80, 170, 30);

        lblLastReceivedTime2 = new JLabel("<html>Last Received Time:<br>Undefined<html>");
        lblLastReceivedTime2.setFont(defaultFont);
        lblLastReceivedTime2.setBounds(15, 130, 170, 30);
        
        pnlProvider2Panel.add(btnProvider2True);
        pnlProvider2Panel.add(btnProvider2False);
        pnlProvider2Panel.add(lblLastProvider2Operation);
        pnlProvider2Panel.add(lblLastReceivedTime2);
        
        // Session Panel
        //pnlRequestor.add(pnlProviderSelection);
        //pnlRequestor.add(pnlSet);
        //pnlRequestor.add(pnlGet);
        //pnlRequestor.add(pnlInvoke);
        pnlRequestor.add(pnlBeginSession);
        pnlRequestor.add(pnlETESession);
        
        // End-To-End
        /*
        pnlSecRequestor.add(pnlSecProviderSelection);
        pnlSecRequestor.add(pnlSecSet);
        pnlSecRequestor.add(pnlSecGet);
        pnlSecRequestor.add(pnlSecInvoke);
        */
        // End-To-End 
        
        pnlAsnychRequestor.add(pnlBeginSet);
        pnlAsnychRequestor.add(pnlBeginGet);
        pnlAsnychRequestor.add(pnlBeginInvoke);        
        
        this.setLayout(null);
        this.setBounds(30,30, 753, 550);
        this.setVisible(true);
        this.add(pnlRequestor);
        //this.add(pnlSecRequestor); //End-To-End
        this.add(pnlAsnychRequestor);
        this.add(pnlProviderPanel);
        this.add(pnlProvider2Panel);
        this.repaint();

        timeUpdater.scheduleAtFixedRate(new TimerUpdate(), 1000, 1000);
    }
    
    private void shutdown(){
        
    }
    
    public void displayGetResults(String providerID, Boolean value) {
        if(providerID.equals(providerOID)){
            if(value != null)
                lblBeginGetResults.setText("Provider 1 Last retrieved value:  " + value.toString());
            else
                lblBeginGetResults.setText("Provider 1 Last retrieved value:  exception");
            updateLastOp(lblLastProviderOperation);
        } else if (providerID.equals(provider2OID)){
            if(value != null)
                lblBeginGetResults2.setText("Provider 2 Last retrieved value:  " + value.toString());
            else
                lblBeginGetResults2.setText("Provider 2 Last retrieved value:  exception");
            updateLastOp(lblLastProvider2Operation);
        }
    }
    
    public void displaySetResults(String providerID) {
        if(providerID.equals(providerOID)){
            lblBeginSetResults.setText("Last Set operation:  Successful");
            if(provider.getActive()) {
                btnProviderTrue.setSelected(true);
                btnProviderFalse.setSelected(false);
            } else {
                btnProviderTrue.setSelected(false);
                btnProviderFalse.setSelected(true);                    
            }
            lblBeginSetResults.setText("Last Set operation:  Successful");
            updateLastOp(lblLastProviderOperation);
        }
        else if(providerID.equals(provider2OID))
        {
            if(provider2.getActive() == true)
            {
                btnProvider2True.setSelected(true);
                btnProvider2False.setSelected(false);
            } else {
                btnProvider2True.setSelected(false);
                btnProvider2False.setSelected(true);                    
            }
            lblBeginSetResults2.setText("Last Set operation:  Successful");
            updateLastOp(lblLastProvider2Operation);
        }
    }
    
    public void displayInvokeResults(String providerID, Boolean value){
        if(providerID.equals(providerOID)){
            if(value != null)
                lblBeginInvokeResults.setText("Provider 1 Last returned value:  " + value.toString());
            else
                lblBeginInvokeResults.setText("Provider 1 Last returned value:  exception");
            lblLastReceivedTime.setText("<html>Last Received Time:<br>" + DateFormat.getTimeInstance().format(provider.getAlarmTime()) + "<html>");
            updateLastOp(lblLastProviderOperation);
        } else if (providerID.equals(provider2OID)){
            if(value != null)
                lblBeginInvokeResults2.setText("Provider 2 Last returned value:  " + value.toString());
            else
                lblBeginInvokeResults2.setText("Provider 2 Last returned value:  exception");
            updateLastOp(lblLastProvider2Operation);
            lblLastReceivedTime2.setText("<html>Last Received Time:<br>" + DateFormat.getTimeInstance().format(provider2.getAlarmTime()) + "<html>");
        }
    }
    
    private void updateLastOp(JLabel updateLabel){
        String lastOpText; 
        if(updateLabel == lblLastProviderOperation)
            lastOpText = "<html>Last Operation:<br>" + provider.getLastOperation() + "<html>";
        else
            lastOpText = "<html>Last Operation:<br>" + provider2.getLastOperation() + "<html>";
        updateLabel.setText(lastOpText);
    }
    
    private class TrainingUIActionListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource().equals(btnGetOp))
            {
                Boolean results = null;
                JLabel opUpdateLabel;
                if(btnProvider1.isSelected()){
                    requestor.setCurrentRequestor(providerOID);
                    opUpdateLabel = lblLastProviderOperation;
                }
                else{
                    requestor.setCurrentRequestor(provider2OID);
                    opUpdateLabel = lblLastProvider2Operation;
                }
                    
                results = requestor.sendGetRequest();
                if(results != null)
                    lblGetResults.setText("Last retrieved value:  " + results.toString());
                else
                    lblGetResults.setText("Last retrieved value:  Undefined");
                
                updateLastOp(opUpdateLabel);
            } else if(e.getSource().equals(btnInvokeOp)) {
                Boolean results;
                JLabel timeUpdateLabel;
                JLabel opUpdateLabel;
                String timestampValue;
                
                if(btnProvider1.isSelected()){
                    requestor.setCurrentRequestor(providerOID);
                    timeUpdateLabel = lblLastReceivedTime;
                    opUpdateLabel = lblLastProviderOperation;
                } else {
                    requestor.setCurrentRequestor(provider2OID);
                    timeUpdateLabel = lblLastReceivedTime2;
                    opUpdateLabel = lblLastProvider2Operation;
                }
                
                results = requestor.sentInvokeRequest( new Date());

                if(results != null)
                    lblInvokeResults.setText("Last returned value:  " + results.toString());
                else
                    lblInvokeResults.setText("Last returned value:  Undefined");
                
                
                if(btnProvider1.isSelected()){
                    timestampValue = DateFormat.getTimeInstance().format(provider.getAlarmTime());
                } else {
                    timestampValue = DateFormat.getTimeInstance().format(provider2.getAlarmTime());
                }
                
                timeUpdateLabel.setText("<html>Last Received Time:<br>" + timestampValue + "<html>");
                updateLastOp(opUpdateLabel);
            } else if(e.getSource().equals(btnSetFalse)) {
                btnSetOp.setEnabled(true);
            } else if(e.getSource().equals(btnSetTrue)) {
                btnSetOp.setEnabled(true);
            } else if(e.getSource().equals(btnSetOp)) {
                boolean setSuccessful;
                boolean setValue;
                JLabel opUpdateLabel;
                
                if(btnSetTrue.isSelected())
                    setValue = true;
                else
                    setValue = false;
                
                if(btnProvider1.isSelected()){
                    requestor.setCurrentRequestor(providerOID);
                    opUpdateLabel = lblLastProviderOperation;
                } else {
                    requestor.setCurrentRequestor(provider2OID);
                    opUpdateLabel = lblLastProvider2Operation;
                }
                
                setSuccessful = requestor.sendSetRequest(setValue);
                
                if(setSuccessful)
                {
                    lblSetResults.setText("Last Set operation:  Successful");
                    if(btnProvider1.isSelected()){
                        if(provider.getActive() == true)
                        {
                            btnProviderTrue.setSelected(true);
                            btnProviderFalse.setSelected(false);
                        } else {
                            btnProviderTrue.setSelected(false);
                            btnProviderFalse.setSelected(true);                    
                        }
                    } else {
                        if(provider2.getActive() == true)
                        {
                            btnProvider2True.setSelected(true);
                            btnProvider2False.setSelected(false);
                        } else {
                            btnProvider2True.setSelected(false);
                            btnProvider2False.setSelected(true);                    
                        }
                    }
                } else {
                    lblSetResults.setText("Last Set operation:  Unsuccessful");
                }
                updateLastOp(opUpdateLabel);
            // ETE
            }else if(e.getSource().equals(btnBeginSession)) {
            	lblSessionResults.setText("Session Enabled: True");
            } else if(e.getSource().equals(btnProviderTrue)) {
            	btnBeginGetOp.setEnabled(true);
            // ETE
            } else if(e.getSource().equals(btnProviderTrue)){
                provider.setActive(true);
            } else if(e.getSource().equals(btnProviderFalse)){
                provider.setActive(false);
            }  else if(e.getSource().equals(btnProvider2True)){
                provider2.setActive(true);
            } else if(e.getSource().equals(btnProvider2False)){
                provider2.setActive(false);
            } else if(e.getSource().equals(btnBeginGetOp)) {
                requestor.sendBeginGetRequest();
            } else if(e.getSource().equals(btnBeginSetOp)) {
                boolean setValue;
                if(btnBeginSetTrue.isSelected())
                    setValue = true;
                else
                    setValue = false;
                
                requestor.sendBeginSetRequest(setValue);
            } else if(e.getSource().equals(btnBeginSetTrue)) {
                btnBeginSetOp.setEnabled(true);
            } else if(e.getSource().equals(btnBeginSetFalse)) {
                btnBeginSetOp.setEnabled(true);
            } else if(e.getSource().equals(btnBeginInvokeOp)) {
                requestor.sendBeginInvokeRequest( new Date());
            }
        }
    }
    
    private class WindowCloseHandler implements WindowListener{
        
        @Override
        public void windowOpened(WindowEvent e) {}
        
        @Override
        public void windowIconified(WindowEvent e) {}
        
        @Override
        public void windowDeiconified(WindowEvent e) {}
        
        @Override
        public void windowDeactivated(WindowEvent e) {}
        
        @Override
        public void windowClosing(WindowEvent e) {
            shutdown();
        }
        
        @Override
        public void windowClosed(WindowEvent e) {}
        
        @Override
        public void windowActivated(WindowEvent e) {}
    }
    
    private class TimerUpdate extends TimerTask {

        @Override
        public void run() {
            now.setTime(System.currentTimeMillis());
            lblCurrentTime.setText(DateFormat.getTimeInstance().format(now));
        }
     
    }
    
    public static void main(String args[]){
        new TrainingUI();
    }
    
}
