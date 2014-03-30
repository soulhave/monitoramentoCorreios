package br.com.correios;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import br.com.consulta.correio.api.Correios;
import br.com.consulta.correio.bean.EncomendaBean;
import br.com.consulta.correio.bean.OcorrenciasEncomendaBean;
import br.com.correios.bean.ItemCorreio;
import br.com.correios.util.Utilitarios;

import com.sun.istack.internal.logging.Logger;
import com.thoughtworks.xstream.XStream;

public class MonitoraCorreios {
	private static final String[] COLUNAS = new String[]{"Descrição","Objeto","Última Atualização","Situação","Local","Observação","Farol"};
	private static final String VAZIO = "";
	private static final String FILE_NAME_OBJETOS_CORREIO_XML = "OBJETOS_CORREIO.xml";
	private static final String TODOS_OS_CAMPOS_SÃO_OBRIGATÓRIOS = "Todos os campos são obrigatórios!";
	private static final String ERRO = "ERRO";
	private static final String DADOS_SALVOS_COM_SUCESSO = "Dados salvos com sucesso";
	
	public static void main(String[] args) {
		trayIcon(); //Cria o Try Icon
	}

	private static void trayIcon() {
		//Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            JOptionPane.showMessageDialog(null, "Não suporta execução em try.");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon =new TrayIcon(Toolkit.getDefaultToolkit().getImage("images/correio_peq.jpg"), "Correio Listener!");
        final SystemTray tray = SystemTray.getSystemTray();
       
        // Create a pop-up menu components
        MenuItem addItem = new MenuItem("Adiciona Item");
        MenuItem displayMenu = new MenuItem("Exibe Itens");
        MenuItem update = new MenuItem("Atualiza Itens");
        MenuItem exitItem = new MenuItem("Sair");
        MenuItem aboutItem = new MenuItem("Sobre");

        /*Incluir Itens*/
        addItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				incluirItem(); //Abre a tela para incluir os novos itens do correio.
			}
		});
        
        /*Incluir Exibir Itens*/
        displayMenu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				exibeGrid();
				
			}
		});
        
        //Add components to pop-up menu
        popup.add(addItem);
        popup.add(displayMenu);
        popup.add(update);
        popup.add(exitItem);
        popup.addSeparator();
        popup.add(aboutItem);
       
        trayIcon.setPopupMenu(popup);
       
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
	}

	
	/**
	 * Inclui itens a serem verificados
	 * @author <a href=mailto:ramon@ciandt.com> Ramon Mendes </a>
	 */
	protected static void incluirItem() {
		final JDialog dialog = new JDialog();
		dialog.setTitle("Adicionar Item");
		dialog.setSize(400, 135);
		dialog.setLayout(null);
		dialog.setResizable(false);
		centraliza(dialog);
		
		
	    JLabel encomenda = new JLabel("Encomenda: ");
	    JLabel correioCod = new JLabel("Código Correio: ");
	    final JTextField encomendaField = new JTextField(40);
	    final JTextField correioCodField = new JTextField(40);

	    encomenda.setDisplayedMnemonic(KeyEvent.VK_U);
	    encomenda.setLabelFor(encomendaField);
	    int HEIGHT = 20;
		encomenda.setBounds(15, 10, 100, HEIGHT);
	    encomenda.setVisible(true);

	    encomendaField.setBounds(115, 10, 260, HEIGHT);
	    encomendaField.setVisible(true);
	    
	    correioCod.setDisplayedMnemonic(KeyEvent.VK_P);
	    correioCod.setLabelFor(correioCodField);
	    correioCod.setBounds(15, 40, 100, HEIGHT);
	    correioCod.setVisible(true);
	    
	    correioCodField.setBounds(115, 40, 150, HEIGHT);
	    correioCodField.setVisible(true);
	    
		JButton jbut = new JButton("Gravar");
		jbut.setBounds(120, 70, 75, HEIGHT+5);

		JButton jbut2 = new JButton("Sair");
		jbut2.setBounds(205, 70, 75, HEIGHT+5);
		
		jbut2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
		
		jbut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!encomendaField.getText().isEmpty() && !correioCodField.getText().isEmpty()){
					ItemCorreio itemCorreio = new ItemCorreio(correioCodField.getText(),encomendaField.getText());
					salvar(itemCorreio);
					dialog.setVisible(false);
				}else{
					JOptionPane.showMessageDialog(null, TODOS_OS_CAMPOS_SÃO_OBRIGATÓRIOS, ERRO, JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	    
	    dialog.getContentPane().add(encomenda);
	    dialog.getContentPane().add(correioCod);
	    dialog.getContentPane().add(encomendaField);
	    dialog.getContentPane().add(correioCodField);
	    dialog.getContentPane().add(jbut);
	    dialog.getContentPane().add(jbut2);
		
		dialog.setVisible(true);
	}
	
	/**
	 * 
	 * @param frame
	 */
	public static void centraliza(JDialog frame) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		if (frameSize.height > screenSize.height)
			frameSize.height = screenSize.height;
		if (frameSize.width > screenSize.width)
			frameSize.width = screenSize.width;
		frame.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
	}	
	
	/**
	 * 
	 * @param correio
	 */
	protected static void salvar(ItemCorreio correio){
		final XStream xstream = new XStream();
		List<ItemCorreio> itensCorreio = itensCorreio(xstream);
		
		final EncomendaBean objetoByKey = Correios.api.getObjetoByKey(correio.getObjeto());
		final OcorrenciasEncomendaBean ocorrencia = obtemSituacoesCorreio(objetoByKey);
		
		correio.setObjeto(objetoByKey.getObjeto());
		correio.setDataUltimaAtualizacao(new Date(System.currentTimeMillis()));
		correio.setHash(objetoByKey.getMd5());
		correio.setLocal(ocorrencia.getLocal());
		correio.setObservacao(ocorrencia.getObservacao());
		correio.setSituacao(ocorrencia.getSituacao());
		itensCorreio.add(correio);

		String xml = xstream.toXML(itensCorreio);
		gravarXml(xml);  
		JOptionPane.showMessageDialog(null, DADOS_SALVOS_COM_SUCESSO);
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private static List<ItemCorreio> itensCorreio(final XStream xstream) {
		Object fromXML = null;
		List<ItemCorreio> itensCorreio = null;
		String ler = ler();

		if(!ler.isEmpty())
			fromXML = xstream.fromXML(ler);
		
		if(fromXML!=null){
			itensCorreio = (List<ItemCorreio>) fromXML;
		}else{
			itensCorreio = new ArrayList<ItemCorreio>();
		}
		return itensCorreio;
	}

	/**
	 * 
	 * @param xml
	 */
	private static void gravarXml(String xml) {
		try {
			FileOutputStream out;
			out = new FileOutputStream(FILE_NAME_OBJETOS_CORREIO_XML);
			PrintStream  p = new PrintStream(out);  
			p.write(xml.getBytes());  
			p.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param objetoByKey
	 * @return
	 */
	private static OcorrenciasEncomendaBean obtemSituacoesCorreio(
			EncomendaBean objetoByKey) {
		List<OcorrenciasEncomendaBean> ocorrenciasEncomendas = objetoByKey.getOcorrenciasEncomendas();

		Collections.sort(ocorrenciasEncomendas, new Comparator<OcorrenciasEncomendaBean>() {
			@Override
			public int compare(OcorrenciasEncomendaBean o1,OcorrenciasEncomendaBean o2) {
				return o2.getDataOcorrencia().compareTo(o1.getDataOcorrencia());
			}
		});
		
		OcorrenciasEncomendaBean ocorrencia = ocorrenciasEncomendas.get(0);
		return ocorrencia;
	}
	
	/**
	 * 
	 * @return
	 */
	private static String ler() {
        File arq = new File(FILE_NAME_OBJETOS_CORREIO_XML);
        StringBuffer texto = new StringBuffer();
		
        if(!arq.isFile()){//Verifica se o arquivo existe.
        	gravarXml(VAZIO);
        	return texto.toString();
        }
        
        try {
			// Indicamos o arquivo que será lido
			FileReader fileReader = new FileReader(arq);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			// String que irá receber cada linha do arquivo
			String linha = VAZIO;

			while ((linha = bufferedReader.readLine()) != null) {
				texto.append(linha);
			}
			// liberamos o fluxo dos objetos ou fechamos o arquivo
			fileReader.close();
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
        return texto.toString();
    }
	
	/**
	 * 
	 */
//		tabela.getColumnModel().addColumnModelListener(new SColumnListener());
//		tabela.getTableHeader().addMouseListener(new SColumnListener());
	protected static void exibeGrid() {
		final JDialog dialog = new JDialog();
		dialog.setTitle("Itens");
		dialog.setSize(800, 600);
		dialog.setLayout(null);
		dialog.setResizable(false);
		centraliza(dialog);
		
		DefaultTableModel model = new DefaultTableModel(obterDadosString(),COLUNAS);
		JTable tabela = new JTableExtension(model);
		tabela = formatarCelulasJTable(tabela);
		
		tabela.setCellSelectionEnabled(false);
		tabela.setColumnSelectionAllowed(false);
		tabela.setRowSelectionAllowed(true);
		tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
/*		ListSelectionModel rowSM = tabela.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				// Ignore extra messages.
				if (e.getValueIsAdjusting())
					return;

				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				if (lsm.isSelectionEmpty()) {
					System.out.println("No rows are selected.");
				} else {
					int selectedRow = lsm.getMinSelectionIndex();
					System.out.println("Row " + selectedRow
							+ " is now selected.");
				}
			}
		});*/
		JScrollPane scroll = formataColumns(tabela);
		scroll.setViewportView(tabela);
		scroll.setBounds(10, 10, 770, 500);
		scroll.setFont(new Font("Arial", Font.BOLD, 8));
		dialog.add(scroll);
		
		dialog.setVisible(true);
	}

	private static JScrollPane formataColumns(JTable tabela) {
		TableColumnModel columnModel = tabela.getColumnModel();
		tabela.getTableHeader().setReorderingAllowed(false);
		columnModel.getColumn(0).setResizable(false); //Descrição
		columnModel.getColumn(0).setWidth(89); //Descrição
		columnModel.getColumn(0).setMinWidth(89); //Descrição
		columnModel.getColumn(1).setResizable(false); //Objeto
		columnModel.getColumn(1).setWidth(87); //Objeto
		columnModel.getColumn(1).setMinWidth(87); //Objeto
		columnModel.getColumn(2).setResizable(false); //Última Atualização
		columnModel.getColumn(2).setWidth(108); //Última Atualização
		columnModel.getColumn(2).setMinWidth(108); //Última Atualização
		columnModel.getColumn(3).setResizable(false); //Situação
		columnModel.getColumn(3).setWidth(96); //Situação
		columnModel.getColumn(3).setMinWidth(96); //Situação
		columnModel.getColumn(4).setResizable(false); //Local
		columnModel.getColumn(4).setWidth(171); //Local
		columnModel.getColumn(4).setMinWidth(171); //Local
		columnModel.getColumn(5).setResizable(false); //Observação
		columnModel.getColumn(5).setWidth(180); //Observação
		columnModel.getColumn(5).setMinWidth(180); //Observação
		columnModel.getColumn(6).setResizable(false);//Farol
		JScrollPane scroll = new JScrollPane();
		return scroll;
	}
	
	/**
	 * 
	 * @return
	 */
	private static Object[][] obterDadosString() {
		List<ItemCorreio> itensCorreio = itensCorreio(new XStream());
		int i = 0;
		String[][] dados = new String[itensCorreio.size()][];
		for (ItemCorreio itemCorreio : itensCorreio) {
			String[] linha = new String[]{"","","","","","",""};
			linha[0] = itemCorreio.getDescricao();
			linha[1] = itemCorreio.getObjeto();
			linha[2] = Utilitarios.formataData(itemCorreio.getDataUltimaAtualizacao());
			linha[3] = itemCorreio.getSituacao();
			linha[4] = itemCorreio.getLocal();
			linha[5] = itemCorreio.getObservacao();
			linha[6] = (itemCorreio.getSituacao().toUpperCase().contains("ENTREGA EFETUADA")?VAZIO:Boolean.toString(itemCorreio.isNovaAtualizacao()));
			dados[i] = linha;
			i++;
		}
		return dados;
	}
	
	private static JTable formatarCelulasJTable(JTable jt){
		for (int i = 0; i < COLUNAS.length; i++) {
			jt.getColumnModel().getColumn(i).setCellRenderer(new StatusColumnCellRenderer());
		}
		return jt;
	}
	
	//*Classe Java//
	
	private static final class JTableExtension extends JTable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3387253662672477156L;

		private JTableExtension(TableModel dm) {
			super(dm);
		}

		public boolean isCellEditable(int rowIndex, int colIndex) {
			  return false; //Disallow the editing of any cell
		}
	}
	private static final class StatusColumnCellRenderer extends DefaultTableCellRenderer {  
		private static final String VAZIO = "";
		private static final String ICON_VERMELHO = "images/vermelho_small.png";
		private static final String ICON_AMARELO = "images/amarelo_small.png";
		private static final String ICON_VERDE = "images/verde_small.png";
		private static final String STRING_TRUE = "true";
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override  
		  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {  
		  
		    //Cells are by default rendered as a JLabel.  
		    JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);  
		  
		    //Get the status for the current row.  
		    if(isSelected){
		    	l.setBackground(new Color(37,148,185));
		    }else if (row%2 == 0) {  
		    	l.setBackground(Color.white);  
		    } else {  
		    	l.setBackground(new Color(220, 220, 220));  
		    }  
		   //Se for coluna 6 renderiza imagem.
		    if(col==6){
		    	l.setHorizontalTextPosition(JLabel.CENTER);
		    	l.setVerticalAlignment(JLabel.CENTER);
		    	l.setHorizontalAlignment(JLabel.CENTER);
		    	l.setVerticalTextPosition(JLabel.CENTER);
		    	renderFarol(value, l);
		    	l.setText(VAZIO);
		    }else{//Se não for verifica se o reg foi atualizado e atualiza.
		    	Object valueAt = table.getValueAt(row, 6);
		    	if(verificaAtualizacao(valueAt)){
		    		l.setFont(new Font("Arial", Font.BOLD, 11));
		    	}else{
		    		l.setFont(new Font("Arial", Font.PLAIN, 10));
		    	}
		    }
		  //Return the JLabel which renders the cell.  
		  return l;  
		  
		}

		/**
		 * Renderiza o farol com a imagem
		 * @param value
		 * @param l
		 */
		private void renderFarol(Object value, JLabel l) {
			if(!l.getText().isEmpty() && verificaAtualizacao(value)){
				l.setIcon(new ImageIcon(ICON_VERMELHO));
			}else if(l.getText().isEmpty()){
					l.setIcon(new ImageIcon(ICON_VERDE));
			}else{
				l.setIcon(new ImageIcon(ICON_AMARELO));
			}
		}

		/**
		 * Verifica se a String é Boolean e true.
		 * @param valueAt
		 * @return
		 */
		private boolean verificaAtualizacao(Object valueAt) {
			return ((String)valueAt).equals(STRING_TRUE);
		}
	}
	
	private static final class SColumnListener extends MouseAdapter implements TableColumnModelListener {

	    private final Logger log = Logger.getLogger(getClass());

	    private boolean resizing = false;
	    private int resizingColumn = -1;
	    private int oldWidth = -1;

	    @Override
	    public void mousePressed(MouseEvent e) {
	        // capture start of resize
	        if(e.getSource() instanceof JTableHeader) {
	            TableColumn tc = ((JTableHeader)e.getSource()).getResizingColumn();
	            if(tc != null) {
	                resizing = true;
	                resizingColumn = tc.getModelIndex();
	                oldWidth = tc.getPreferredWidth();
	            } 
	        }   
	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
	        // column moved
	        // column resized
	        if(resizing) {
	            if(e.getSource() instanceof JTableHeader) {
	                TableColumn tc = ((JTableHeader)e.getSource()).getColumnModel().getColumn(resizingColumn);
	                if(tc != null) {
	                    int newWidth = tc.getPreferredWidth();
	                    if(newWidth != oldWidth) {
	                        log.info(resizingColumn+"-Coluna Alterada: " +resizingColumn+" -> "+newWidth);
	                    }
	                }
	            }   
	        }
	        resizing = false;
	    }
		
		@Override
		public void columnAdded(TableColumnModelEvent e) {
			
		}

		@Override
		public void columnRemoved(TableColumnModelEvent e) {
			
		}

		@Override
		public void columnMoved(TableColumnModelEvent e) {
			
		}

		@Override
		public void columnMarginChanged(ChangeEvent e) {
			
		}

		@Override
		public void columnSelectionChanged(ListSelectionEvent e) {
			
		}
		
	}
}
