package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ViewDialog {
    public static void showView(Component parent, String title, List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No data available for this view.", title, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // build columns from first row keys
        Map<String, Object> first = rows.get(0);
        String[] columns = first.keySet().toArray(new String[0]);

        DefaultTableModel model = new DefaultTableModel(columns, 0);
        for (Map<String, Object> row : rows) {
            Object[] vals = new Object[columns.length];
            for (int i = 0; i < columns.length; i++) {
                vals[i] = row.get(columns[i]);
            }
            model.addRow(vals);
        }

        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(900, 400));

        JOptionPane.showMessageDialog(parent, scroll, title, JOptionPane.PLAIN_MESSAGE);
    }
}
