/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package jpiere.base.plugin.org.adempiere.process;

import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

/**
 * JPIERE-0590: JPiere Storage Cleanup
 * 
 * ref: org.compiere.process.StorageCleanup
 *	
 *  @author Jorg Janke
 *  @author Hideaki Hagiwara
 */
@org.adempiere.base.annotation.Process
public class JPiereStorageCleanup extends SvrProcess
{	
	
	int p_Date = 0;
	/**
	 *  Prepare - e.g., get Parameters.
	 */
	protected void prepare()
	{
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("Date"))
				p_Date = para[i].getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}	//	prepare

	/**
	 * 	Process
	 *	@return message
	 *	@throws Exception
	 */
	protected String doIt () throws Exception
	{
		int deleteNoASI = 0;
		int deleteASI = 0;
		int deleteReserve = 0;	
		
		log.info("");
		//	Clean up empty Storage with no asi
		String sql = "DELETE FROM M_StorageOnHand "
			+ "WHERE QtyOnHand = 0 AND M_AttributeSetInstance_ID=0 "
			+ " AND Created < getDate()-" + p_Date;
		deleteNoASI = DB.executeUpdateEx(sql, get_TrxName());
		if (log.isLoggable(Level.INFO)) log.info("Delete Empty #" + deleteNoASI);

		//	Clean up empty Storage with asi but not using serial/lot
		sql = "DELETE FROM M_StorageOnHand "
			+ "WHERE QtyOnHand = 0 AND M_AttributeSetInstance_ID > 0 "
			+ " AND Created < getDate()-" + p_Date
			+ " AND EXISTS (SELECT 1 FROM M_AttributeSetInstance a WHERE a.M_AttributeSetInstance_ID=M_StorageOnHand.M_AttributeSetInstance_ID"
			+ " AND a.Lot IS NULL AND a.SerNo IS NULL) ";
		deleteASI = DB.executeUpdateEx(sql, get_TrxName());
		if (log.isLoggable(Level.INFO)) log.info("Delete Empty #" + deleteASI);
			
		//	Clean up empty Reservation Storage
		sql = "DELETE FROM M_StorageReservation "
			+ "WHERE Qty = 0"
			+ " AND Created < getDate()-" + p_Date;
		deleteReserve = DB.executeUpdateEx(sql, get_TrxName());
		if (log.isLoggable(Level.INFO)) log.info("Delete Empty #" + deleteReserve);
		
		return "Count of Delete Record in M_StorageOnHand Table = "+ (deleteNoASI+deleteASI) + " : Count of Delete Record in M_StorageReservation Table = " + deleteReserve;
	}	//	doIt
	
}	//	StorageCleanup
