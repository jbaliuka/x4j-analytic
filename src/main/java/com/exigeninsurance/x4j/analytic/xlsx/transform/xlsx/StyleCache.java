/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/

package com.exigeninsurance.x4j.analytic.xlsx.transform.xlsx;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.exigeninsurance.x4j.analytic.model.Money;


public final class StyleCache {

	private final Map<XLSXCellNode, Long> styles = new HashMap<XLSXCellNode, Long>();
	private final CurrencyStyleCache currencyStyles = new CurrencyStyleCache();

	public boolean containsDateStyle(XLSXCellNode node) {
		return styles.containsKey(node);
	}

	public long getDateStyle(XLSXCellNode node) {
		return styles.get(node);
	}

	public boolean containsCurrencyStyle(XLSXCellNode node, String currencycd) {
		return currencyStyles.containsStyle(node, currencycd);
	}

	public long getCurrencyStyle(XLSXCellNode node, String currencyCd) {
		return currencyStyles.get(node, currencyCd);
	}

	public void putStyle(Object value, XLSXCellNode node, long style) {
		if (value instanceof Date) {
			styles.put(node, style);
		}
		else {
			currencyStyles.put(node, ((Money) value).getCurrencyCd(), style);
		}
	}

	private static class CurrencyStyleCache {
		private Map<StyleKey, Long> styles = new HashMap<StyleKey, Long>();

		public boolean containsStyle(XLSXCellNode node, String currencyCd) {
			return styles.containsKey(new StyleKey(node, currencyCd));
		}

		public void put(XLSXCellNode node, String currencyCd, Long style) {
			styles.put(new StyleKey(node, currencyCd), style);
		}

		public long get(XLSXCellNode node, String currencyCd) {
			return styles.get(new StyleKey(node, currencyCd));
		}

		private static class StyleKey {
			private XLSXCellNode node;
			private String currencyCd;

			private StyleKey(XLSXCellNode node, String currencyCd) {
				this.node = node;
				this.currencyCd = currencyCd;
			}

			@Override
			public boolean equals(Object o) {
				if (this == o){
					return true;					
				}
				if (o == null || getClass() != o.getClass()){
					return false;
				}

				StyleKey styleKey = (StyleKey) o;

				if (currencyCd != null ? !currencyCd.equals(styleKey.currencyCd) : styleKey.currencyCd != null){
					return false;
				}

                return node.equals(styleKey.node);

            }

			@Override
			public int hashCode() {
				int result = node.hashCode();
				result = 31 * result + (currencyCd != null ? currencyCd.hashCode() : 0);
				return result;
			}
		}
	}
}
