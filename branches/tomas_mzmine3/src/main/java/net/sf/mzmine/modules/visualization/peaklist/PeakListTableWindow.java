/*
 * Copyright 2006-2014 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.modules.visualization.peaklist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable.PrintMode;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.visualization.peaklist.table.PeakListTable;
import net.sf.mzmine.modules.visualization.peaklist.table.PeakListTableColumnModel;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.util.ExitCode;

public class PeakListTableWindow extends JFrame implements
		ActionListener {

	private JScrollPane scrollPane;

	private PeakListTable table;

	private ParameterSet parameters;

	/**
	 * Constructor: initializes an empty visualizer
	 */
	PeakListTableWindow(PeakList peakList, ParameterSet parameters) {

		super(peakList.getName());

		this.parameters = parameters;

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBackground(Color.white);

		// Build toolbar
		PeakListTableToolBar toolBar = new PeakListTableToolBar(this);
		add(toolBar, BorderLayout.EAST);

		// Build table
		table = new PeakListTable(this, parameters, peakList);

		scrollPane = new JScrollPane(table);
		
		add(scrollPane, BorderLayout.CENTER);

		pack();

	}

    public int getJScrollSizeWidth() {
        return table.getPreferredSize().width;  
    } 
    public int getJScrollSizeHeight() {
    	return table.getPreferredSize().height;  
    } 

	/**
	 * Methods for ActionListener interface implementation
	 */
	public void actionPerformed(ActionEvent event) {

		String command = event.getActionCommand();

		if (command.equals("PROPERTIES")) {

			ExitCode exitCode = parameters.showSetupDialog();
			if (exitCode == ExitCode.OK) {
				int rowHeight = parameters.getParameter(
						PeakListTableParameters.rowHeight).getValue();
				table.setRowHeight(rowHeight);

				PeakListTableColumnModel cm = (PeakListTableColumnModel) table
						.getColumnModel();
				cm.createColumns();

			}
		}
		
		if (command.equals("AUTOCOLUMNWIDTH")) {
	        // Auto size column width based on data
	        for (int column = 0; column < table.getColumnCount(); column++)
	        {
	            TableColumn tableColumn = table.getColumnModel().getColumn(column);
	            if(tableColumn.getHeaderValue() != "Peak shape" && tableColumn.getHeaderValue() != "Status") {
		            TableCellRenderer renderer = tableColumn.getHeaderRenderer();
		            if (renderer == null) {
		                renderer = table.getTableHeader().getDefaultRenderer();
		            }
		            Component component = renderer.getTableCellRendererComponent(table, tableColumn.getHeaderValue(), false, false, -1, column);
		            int preferredWidth = component.getPreferredSize().width+20;
		            tableColumn.setPreferredWidth( preferredWidth );
	            }
	        }
		}

		if (command.equals("PRINT")) {
			try {
				table.print(PrintMode.FIT_WIDTH);
			} catch (PrinterException e) {
				MZmineCore.getDesktop().displayException(e);
			}
		}
	}
}
