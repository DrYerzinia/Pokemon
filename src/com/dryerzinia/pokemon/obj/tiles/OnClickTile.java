package com.dryerzinia.pokemon.obj.tiles;

import java.util.HashMap;

import com.dryerzinia.pokemon.event.EventCore;
import com.dryerzinia.pokemon.util.JSONObject;

public class OnClickTile extends Tile {

	private static final long serialVersionUID = 3535350761004531440L;

	private int onClickEventID;

	public OnClickTile() {
	}

	public OnClickTile(int onClickEventID) {

		this.onClickEventID = onClickEventID;

	}

	public void click(){

		EventCore.fireEvent(onClickEventID);

	}

	@Override
	public void fromJSON(HashMap<String, Object> json){

		super.fromJSON(json);

		onClickEventID = ((Float)json.get("onClickEventID")).intValue();

	}

	@Override
	public String toJSON() throws IllegalAccessException {

		return JSONObject.defaultToJSON(this);

	}

}
