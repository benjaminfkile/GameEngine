package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.BowlTerrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();

		// *********TERRAIN TEXTURE STUFF***********
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy5"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("sand"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("stonePath"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		BowlTerrain bowlTerrain = new BowlTerrain(0,-1,loader, texturePack, blendMap, "heightmap");
		//BowlTerrain undergroundTerrain = new BowlTerrain(0,-1,loader, texturePack, blendMap, "heightmap");
		
		// *****************************************

		TexturedModel tree = new TexturedModel(OBJLoader.loadObjModel("pine", loader), new ModelTexture(loader.loadTexture("pine")));
		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader), new ModelTexture(loader.loadTexture("grassTexture")));
		TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader), new ModelTexture(loader.loadTexture("flower")));
		TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp", loader), new ModelTexture(loader.loadTexture("lamp")));
		//TexturedModel wall = new TexturedModel(OBJLoader.loadObjModel("wall", loader), new ModelTexture(loader.loadTexture("wallTexture")));

		ModelTexture fernTexture = new ModelTexture(loader.loadTexture("fern"));
		fernTexture.setNumberOfRows(2);
		TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader), fernTexture);


		tree.getTexture().setReflectivity(0);
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		grass.getTexture().setReflectivity(1);
		flower.getTexture().setHasTransparency(true);
		flower.getTexture().setUseFakeLighting(true);
		flower.getTexture().setReflectivity(0);
		fern.getTexture().setHasTransparency(true);
		fern.getTexture().setUseFakeLighting(false);
		fern.getTexture().setReflectivity(0);

		lamp.getTexture().setReflectivity(0);
		lamp.getTexture().setUseFakeLighting(true);
		lamp.getTexture().setHasTransparency(true);

		List <Light> lights = new ArrayList<Light>();

		lights.add(new Light(new Vector3f(0,1000,0), new Vector3f(0.4f,0.4f,0.4f))); // main sun light
		lights.add(new Light(new Vector3f(3200,1000,-3200), new Vector3f(0.4f,0.4f,0.4f))); // main sun light
		lights.add(new Light(new Vector3f(1600,1000,-1600), new Vector3f(0.4f,0.4f,0.4f))); // main sun light
		


		List<Entity> entities = new ArrayList<Entity>();



		float x=0, y=0, z=0;
		x = 400;
		z = -490;
		y = bowlTerrain.getHeightOfTerrain(x, z);
		float yOffset = 15; // this is approximately the height of the lamp post which we add to the height of the light

		entities.add(new Entity(lamp, new Vector3f(x, y, z), 0, 0, 0, 1));
		lights.add(new Light(new Vector3f(x, y+yOffset, z), new Vector3f(2, 0, 0), new Vector3f(1.0f, 0.01f, 0.002f)));

		x = +490;
		z = -400-60;
		y = bowlTerrain.getHeightOfTerrain(x, z);
		entities.add(new Entity(lamp, new Vector3f(x, y, z), 0, 0, 0, 1));
		lights.add(new Light(new Vector3f(x, y+yOffset, z), new Vector3f(2, 2, 0), new Vector3f(1.0f, 0.01f, 0.002f)));

		x = +490;
		z = -350;
		y = bowlTerrain.getHeightOfTerrain(x, z);
		entities.add(new Entity(lamp, new Vector3f(x, y, z), 0, 0, 0, 1));
		lights.add(new Light(new Vector3f(x, y+yOffset, z), new Vector3f(0, 2, 0), new Vector3f(1.0f, 0.01f, 0.002f)));



		Random random = new Random();


		for (int i = 0; i < 500; i++) {
			x = random.nextFloat() * 3200;
			z = random.nextFloat() * -3200;
			y = bowlTerrain.getHeightOfTerrain(x, z);
			entities.add(new Entity(fern, random.nextInt(16), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 1.5f));
			entities.add(new Entity(grass, new Vector3f(x, y, z), 0, 0, 0, 1.8f));
			entities.add(new Entity(flower, new Vector3f(x, y, z), 0, 0, 0, 2.3f));
		}


		int i = 0;
		do{ 
			i++;
			x = random.nextFloat() * 3200;
			z = random.nextFloat() * -3200;
			y = bowlTerrain.getHeightOfTerrain(x, z);
			entities.add(new Entity(tree, new Vector3f(x, y, z), 0, 0, 0, random.nextFloat() * 1 + 20 ));
		}while(i<250);


		MasterRenderer renderer = new MasterRenderer(loader);


		TexturedModel avatar = new TexturedModel(OBJLoader.loadObjModel("bunny",  loader), new ModelTexture(loader.loadTexture("white")));

		Player player = new Player(avatar, new Vector3f(1600,0,-1600), 0,270,0,1);
		Camera camera = new Camera(player);

		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		GuiTexture gui3 = new GuiTexture(loader.loadTexture("health"), new Vector2f(-0.74f, 0.925f), new Vector2f(0.25f, 0.25f));
		guis.add(gui3);

		GuiRenderer guiRenderer = new GuiRenderer(loader);

		while(!Display.isCloseRequested()) {
			camera.move();
			player.move(bowlTerrain);
			renderer.processEntity(player);
			renderer.processTerrain(bowlTerrain);

			for (Entity entity : entities){
				renderer.processEntity(entity);
			}
			renderer.render(lights, camera);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
		}

		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();

		DisplayManager.closeDisplay();
		System.out.println("Entities: " + entities.size());
	}

}
