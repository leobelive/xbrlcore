package net.gbicc.xbrl.ent.model;

/**
 * 存放分类标准的属性
 * 
 * @author joephoenix
 * 
 */
public class TaxonomyInfo {

	/**
	 * 分类标准根地址，虚拟路径
	 */
	private String taxonomyBase;
	/**
	 * 入口文件的地址，采用uri绝对路径的方式
	 */
	private String importLocation;

	public String getTaxonomyBase() {
		return taxonomyBase;
	}

	public void setTaxonomyBase(String taxonomyBase) {
		this.taxonomyBase = taxonomyBase;
	}

	public String getImportLocation() {
		return importLocation;
	}

	public void setImportLocation(String importLocation) {
		this.importLocation = importLocation;
	}
}
