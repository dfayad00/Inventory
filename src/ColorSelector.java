import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class ColorSelector extends JFrame {

    private final JTextField quantityField;
    private final JComboBox<String> colorList;
    private ArrayList<ColorItem> inventory;
    private JTable inventoryTable;

    public ColorSelector() {
        setTitle("Color Selector");
        //setSize(400, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        colorList = new JComboBox<String>();

        colorList.addActionListener(e -> {
            String color = (String) colorList.getSelectedItem();
            assert color != null;
        });

        add(colorList);

        JLabel quantityLabel = new JLabel("Quantity:");
        add(quantityLabel);

        quantityField = new JTextField(5);
        add(quantityField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new SaveButtonListener());
        add(saveButton);

        createInventoryTable();

        inventory = new ArrayList<>();
        loadInventory();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.getWidth() * 0.4);
        int height = (int) (screenSize.getHeight() * 0.4);
        setSize(width, height);
    }

    private void createInventoryTable() {
        inventory = new ArrayList<>();
        loadInventory();
        inventoryTable = new JTable(new InventoryTableModel(inventory));
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        add(scrollPane, BorderLayout.SOUTH);
    }


    private class InventoryTableModel extends AbstractTableModel {
        private ArrayList<ColorItem> inventory;
        private String[] columnNames = {"Color", "Quantity"};

        public InventoryTableModel(ArrayList<ColorItem> inventory) {
            this.inventory = inventory;
        }

        public int getRowCount() {
            return inventory.size();
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            ColorItem item = inventory.get(rowIndex);
            if (columnIndex == 0) {
                return item.getColor();
            } else {
                return item.getQuantity();
            }
        }

        public String getColumnName(int column) {
            return columnNames[column];
        }
    }

    private void loadInventory() {
        colorList.removeAllItems();
        try (BufferedReader br = new BufferedReader(new FileReader("inventory.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(": ");
                String color = parts[0];
                int quantity = Integer.parseInt(parts[1]);
                inventory.add(new ColorItem(color, quantity));
                colorList.addItem(color);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveInventory() {
        try (FileWriter fw = new FileWriter("inventory.txt");
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            for (ColorItem item: inventory) {
                out.println(item.getColor() + ": " + item.getQuantity());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SaveButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String color = (String) colorList.getSelectedItem();
            int quantity = Integer.parseInt(quantityField.getText());

            for(ColorItem item : inventory) {
                if(item.getColor().equalsIgnoreCase(color)) {
                    item.setQuantity(quantity);
                    break;
                }
            }
            saveInventory();
        }
    }

    public static void main(String[] args) {
        ColorSelector colorSelector = new ColorSelector();
        colorSelector.setVisible(true);
    }

    private static class ColorItem {
        private final String color;
        private int quantity;

        public ColorItem(String color, int quantity) {
            this.color = color;
            this.quantity = quantity;
        }

        public String getColor() {
            return color;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

}
