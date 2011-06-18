package com.fahimk.spreadshirt;

public class ShirtType {
	long id, typeId;
	int drawable;
	String name;
	String description;
	public ShirtColor[] colors;
	
	public ShirtType(long id, long typeId, String name, int drawable, String description, ShirtColor[] colors) {
		super();
		this.id = id;
		this.typeId = typeId;
		this.name = name;
		this.drawable = drawable;
		this.description = description;
		this.colors = colors;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTypeId() {
		return typeId;
	}

	public void setTypeId(long typeId) {
		this.typeId = typeId;
	}

	public int getDrawable() {
		return drawable;
	}

	public void setDrawable(int drawable) {
		this.drawable = drawable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	
	
}
