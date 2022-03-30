package engineTester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.OBJFileLoader;
import particles.Particle;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		TextMaster.init(loader);
		MasterRenderer renderer = new MasterRenderer(loader);
		ParticleMaster.init(loader, renderer.getProjectionMatrix());
		
		
		FontType font = new FontType(loader.loadTexture("verdana"), new File("res/verdana.fnt"));
		GUIText text = new GUIText("OpenGL", 3f, font, new Vector2f(-0.4f, 0.85f), 1f, true);
		text.setColour(1, 1, 1);

		// *********TERRAIN TEXTURE STUFF**********
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("mosspath256"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture,
				gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		// *****************************************

		TexturedModel rocks = new TexturedModel(OBJFileLoader.loadOBJ("rocks", loader),
				new ModelTexture(loader.loadTexture("rocks")));
		TexturedModel cherry = new TexturedModel(OBJFileLoader.loadOBJ("cherry", loader),
				new ModelTexture(loader.loadTexture("cherry")));
		

		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		fernTextureAtlas.setNumberOfRows(2);

		TexturedModel fern = new TexturedModel(OBJFileLoader.loadOBJ("fern", loader),
				fernTextureAtlas);

		TexturedModel bobble = new TexturedModel(OBJFileLoader.loadOBJ("pine", loader),
				new ModelTexture(loader.loadTexture("pine")));
		bobble.getTexture().setHasTransparency(true);

		fern.getTexture().setHasTransparency(true);

		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");
		List<Terrain> terrains = new ArrayList<Terrain>();
		terrains.add(terrain);

		TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp", loader),
				new ModelTexture(loader.loadTexture("lamp")));
		lamp.getTexture().setUseFakeLighting(true);

		List<Entity> entities = new ArrayList<Entity>();
		List<Entity> normalMapEntities = new ArrayList<Entity>();
		
		//******************NORMAL MAP MODELS************************
		
		TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),
				new ModelTexture(loader.loadTexture("barrel")));
		barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
		barrelModel.getTexture().setShineDamper(10);
		barrelModel.getTexture().setReflectivity(0.5f);
		
		TexturedModel crateModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader),
				new ModelTexture(loader.loadTexture("crate")));
		crateModel.getTexture().setNormalMap(loader.loadTexture("crateNormal"));
		crateModel.getTexture().setShineDamper(10);
		crateModel.getTexture().setReflectivity(0.5f);
		
		TexturedModel boulderModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader),
				new ModelTexture(loader.loadTexture("boulder")));
		boulderModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
		boulderModel.getTexture().setShineDamper(10);
		boulderModel.getTexture().setReflectivity(0.4f);
		
		
		//************ENTITIES*******************
		
		Entity entity = new Entity(barrelModel, new Vector3f(75, 10, -75), 0, 0, 0, 1f);
		Entity entity2 = new Entity(boulderModel, new Vector3f(85, 10, -75), 0, 0, 0, 1f);
		Entity entity3 = new Entity(crateModel, new Vector3f(65, 10, -75), 0, 0, 0, 0.04f);
		normalMapEntities.add(entity);
		normalMapEntities.add(entity2);
		normalMapEntities.add(entity3);
		
		Random random = new Random(5666778);
		for (int i = 0; i < 320; i++) {
			if (i % 3 == 0) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * -800;
				
				float y = terrain.getHeightOfTerrain(x, z);

				if(y>0) {
				entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z), 0,
						random.nextFloat() * 360, 0, 0.9f));
				}
				
			}
			if (i % 1 == 0) {

				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * -800;
				if ((x > 50 && x < 100) || (z < -50 && z > -100)) {

				} else {
					float y = terrain.getHeightOfTerrain(x, z);
					if(y>0) {
					
					entities.add(new Entity(bobble, 1, new Vector3f(x, y, z), 0,
							random.nextFloat() * 360, 0, random.nextFloat() * 0.6f + 0.8f));
					}
					
					
					
					
					
				}
			}
			if(i%20==0) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * -800;
				
				if ((x > 50 && x < 100) || (z < -50 && z > -100)) {

				} else {
					float y = terrain.getHeightOfTerrain(x, z);
					
					if(y>0) {
						entities.add(new Entity(cherry, 1, new Vector3f(x, y, z), 0,
								random.nextFloat() * 360, 0, random.nextFloat() * 1.6f + 0.8f));
					}
				}
			
			}
		}
		
		for(int i=0;i<20;i++) {
			float x = 400 + random.nextFloat() *300;
			float z = -400 + random.nextFloat() *300;
			
			float y = terrain.getHeightOfTerrain(x, z);
			
			normalMapEntities.add(new Entity(boulderModel, new Vector3f(x,y,z), random.nextFloat() * 50 -30, 0, random.nextFloat() * 50 -30, random.nextFloat() * 0.6f + 0.8f));
		}
		
		entities.add(new Entity(rocks, new Vector3f(400, 0.3f, -400), 0, 0, 0, 400));
		
		//*******************OTHER SETUP***************

		List<Light> lights = new ArrayList<Light>();
		Light sun = new Light(new Vector3f(10000, 10000, -10000), new Vector3f(0.8f, 0.8f, 0.8f));
		lights.add(sun);
		
		float height = terrain.getHeightOfTerrain(100,-293);
		
		lights.add(new Light(new Vector3f(100,25,-293),new Vector3f(2,0,2), new Vector3f(1,0.01f,0.002f)));
		entities.add(new Entity(lamp,new Vector3f(100,height,-293),0,0,0,1));
		
		
		height = terrain.getHeightOfTerrain(160,-193);
		lights.add(new Light(new Vector3f(160,15,-193),new Vector3f(0,2,2), new Vector3f(1,0.01f,0.002f)));
		entities.add(new Entity(lamp,new Vector3f(160,height,-193),0,0,0,1));
		
		
		height = terrain.getHeightOfTerrain(220,-393);
		lights.add(new Light(new Vector3f(500,15,-380),new Vector3f(2,0,0), new Vector3f(1,0.01f,0.002f)));
		//entities.add(new Entity(lamp,new Vector3f(220,height,-393),0,0,0,1));
		
		
		
		

		RawModel playerModel = OBJLoader.loadObjModel("person", loader);
		TexturedModel playerGame = new TexturedModel(playerModel, new ModelTexture(
				loader.loadTexture("blankTexture")));

		Player player = new Player(playerGame, new Vector3f(500, 5, -320), 0, 100, 0, 0.6f);
		entities.add(player);
		Camera camera = new Camera(player);
		List<GuiTexture> guiTextures = new ArrayList<GuiTexture>();
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);
	
		//**********Water Renderer Set-up************************
		
		WaterFrameBuffers buffers = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		for(int i = 1;i< 5;i++) {
			for(int j=1;j<5;j++) {
				waters.add(new WaterTile(i*160, -j *160, 0));
			}
		}
		//fire1
		ParticleTexture fireTexture = new ParticleTexture(loader.loadTexture("fire"),8);
		ParticleSystem fireSystem = new ParticleSystem(fireTexture,5,0.5f,-0.008f,7,30f);
		fireSystem.setDirection(new Vector3f(0,1,0), 0);
		//fireSystem.setLifeError(0.1f);
		//fireSystem.setSpeedError(0.1f);
		//fireSystem.setScaleError(0.1f);
		fireSystem.randomizeRotation();
		
		
		 //fire2
        ParticleTexture fireTex = new ParticleTexture(loader.loadTexture("Flame_Particle"), 1);
        ParticleSystem fireSys = new ParticleSystem(fireTex, 100, 10, 0.1f, 2, 2f);
        fireSys.setDirection(new Vector3f(0, 2, 0), 0.1f);
        fireSys.setLifeError(0.2f);
        fireSys.setSpeedError(0.6f);
        fireSys.setScaleError(1f);
        fireSys.randomizeRotation();
		
		//smoke
        ParticleTexture smokeTexture = new ParticleTexture(loader.loadTexture("smoke"), 3);
        ParticleSystem smokeSystem = new ParticleSystem(smokeTexture, 75, 5, -0.01f, 3, 10f);
        smokeSystem.setDirection(new Vector3f(0, 2, 0), 0.1f);
        smokeSystem.setLifeError(0.2f);
        smokeSystem.setSpeedError(0.6f);
        smokeSystem.setScaleError(1f);
        smokeSystem.randomizeRotation();
        
      //smoke2
        ParticleTexture smokeTex = new ParticleTexture(loader.loadTexture("Smoke-Particle"), 1);
        ParticleSystem smokeSys = new ParticleSystem(smokeTex, 100, 4, -0.1f, 2, 2f);
        smokeSystem.setDirection(new Vector3f(0, 2, 0), 0.2f);
        smokeSystem.setLifeError(0.2f);
        smokeSystem.setSpeedError(0.6f);
        smokeSystem.setScaleError(1f);
        smokeSystem.randomizeRotation();
        
        ParticleTexture starTex = new ParticleTexture(loader.loadTexture("particleStar"), 1);
        ParticleSystem starSys = new ParticleSystem(starTex, 50, 10, 0.1f, 20, 2f);
        smokeSystem.setDirection(new Vector3f(1, 2, 1), 0.5f);
        smokeSystem.setLifeError(0.2f);
        smokeSystem.setSpeedError(0.6f);
        smokeSystem.setScaleError(1f);
        smokeSystem.randomizeRotation();
        
		
		//****************Game Loop Below*********************

		while (!Display.isCloseRequested()) {
			player.move(terrain);
			camera.move();
			picker.update();
			
			fireSystem.generateParticles(new Vector3f(500,10,-380));
			
			smokeSystem.generateParticles(new Vector3f(500,25,-380));
			
			fireSys.generateParticles(new Vector3f(400,10,-300));
			
			smokeSys.generateParticles(new Vector3f(400,20,-300));
			
			starSys.generateParticles(new Vector3f(400,100,-400));
			
			ParticleMaster.update(camera);
			
			
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			//render reflection teture
			buffers.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - waters.get(0).getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera,
					new Vector4f(0, 1, 0, -waters.get(0).getHeight() + 1));
			camera.getPosition().y += distance;
			camera.invertPitch();
			
			//render refraction texture
			buffers.bindRefractionFrameBuffer();
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera,
					new Vector4f(0, -1, 0, waters.get(0).getHeight() +0.2f));
			
			//render to screen
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			buffers.unbindCurrentFrameBuffer();	
			renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, 100000));	
			waterRenderer.render(waters, camera, sun);
			
			ParticleMaster.renderParticles(camera);
			
			guiRenderer.render(guiTextures);
			TextMaster.render();
			
			DisplayManager.updateDisplay();
		}

		//*********Clean Up Below**************
		ParticleMaster.cleanUp();
		TextMaster.cleanUp();
		buffers.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}


}
