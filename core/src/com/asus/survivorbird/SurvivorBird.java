package com.asus.survivorbird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;

import java.util.Random;

import javafx.scene.text.Font;

public class SurvivorBird extends ApplicationAdapter {


	SpriteBatch batch;  //hareketli nesne çizdirmek için ?
	Texture background;//resimleri atamak için
	Texture ship;
	Texture enemy1,enemy2,enemy3;
	float shipX=0; //karakterin yerini tespit
	float shipY=0;

	int gameState = 0; //oyunun durumu 0-başlamadı 1-başladı 2-bitti
	float velocity = 0; //düşme hızı(başka bir isim verilebilirdi )
	float gravity = 0.8f; // yer çekimi

	int numberOfEnemy=4; //düşmanlar kaçarlı gruplardan oluşacak
	float [] enemyX = new float[numberOfEnemy]; //düşmanların x konumu
	float [] enemyOffSet = new float[numberOfEnemy]; // düşmanların y ekseni
	float [] enemyOffSet2 = new float[numberOfEnemy];
	float [] enemyOffSet3 = new float[numberOfEnemy];

	float[] yNumbers;

	float distance=0; // 2 düşman arası mesafe
	float enemyVelocity=8; //düşman gelme hızı
	Random rand;
	float randomNum;
	int score = 0;
	int scoredEnemy=0; //hangi düşman  0-1-2-3 her 4 düşman grubu var her birini geçince skor artacak
	BitmapFont fontScore; //skoru tuttuğumuz font
	BitmapFont fontGameOver; //game over yazısını tuttuğumuz font

	Circle shipCircle; // Çarpışmaları anlamak için çember kullandık
	Circle[] enemyCircle;
	Circle[] enemyCircle2;
	Circle[] enemyCircle3;


	//oyun başlarken 1 kere çalışıp tanımlamaların yapıldığı yer
	@Override
	public void create () {
		 rand = new Random();

		batch = new SpriteBatch();

		background = new Texture("background7.png");
		ship = new Texture("ship2.png");
		enemy1 = new Texture("enemy.png");
		enemy2 = new Texture("enemy.png");
		enemy3 = new Texture("enemy.png");

		shipX=Gdx.graphics.getWidth() / 4;
		shipY=Gdx.graphics.getHeight() / 2;



		distance = Gdx.graphics.getWidth()/2;//İki enemy arasındaki yatay uzaklık--geliş sıklığı

		shipCircle = new Circle();
		enemyCircle = new Circle[numberOfEnemy];
		enemyCircle2 = new Circle[numberOfEnemy];
		enemyCircle3 = new Circle[numberOfEnemy];

		fontScore= new BitmapFont();
		fontScore.setColor(Color.WHITE);
		fontScore.getData().scale(5);

		fontGameOver= new BitmapFont();
		fontGameOver.setColor(Color.WHITE);
		fontGameOver.getData().scale(7);

		//her grupta 4 adam var her seferinde her grubun 1 adamını ayarlıyor
		//i=0 için bütün 0. adamları i=1 için 1. adamları
		for(int i=0; i<numberOfEnemy;i++){
			//Her grubun x konumu
			enemyX[i] = Gdx.graphics.getWidth()+ship.getWidth()+i*distance;

			//düşmanın y eksenindeki yeri için
			//randomNum=rand.nextFloat();
			/*enemyOffSet[i] = ((rand.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-200));
			enemyOffSet2[i] = ((rand.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-200));
			enemyOffSet3[i] = ((rand.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-200));*/
			yNumbers = yEkseni();
			enemyOffSet[i] = (yNumbers[0]);
			enemyOffSet2[i] = (yNumbers[1]);
			enemyOffSet3[i] = (yNumbers[2]);

			enemyCircle[i] = new Circle();
			enemyCircle2[i] = new Circle();
			enemyCircle3[i] = new Circle();

		}
	}

	@Override
	public void render () {

		//arkaplanı çizdir
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		//Oyun başladı  -- Touched
		if(gameState==1){

			//SKORU ARTTIR
			if(enemyX[scoredEnemy]<shipX){
				score++;
				enemyVelocity+=score/20f;
				//4 lü grup bitince başa dönecek
				if (scoredEnemy<numberOfEnemy-1){
					scoredEnemy++;
				}else {
					scoredEnemy=0;
				}
			}

			//Her tıklamada ne kadar yukarı zıplayacak
			if (Gdx.input.justTouched()){
				//düşme hızını eksi bir değere çeviriyoruz ve adam yukarı çıkıyor
				//gravity her seferinde eklenerek eksi değeri en sonunda sıfıra sonrada artıya çeviriyor
				//ve adam biraz yükseldikten sonra durup düşmeye başlıyor
				//motor bozulunda random number kullanabilirsin mesela !
				//randomNum = rand.nextInt(20-8+1)+8;                  +++++++++++

				if(shipY<Gdx.graphics.getHeight()-Gdx.graphics.getHeight()/10){
					velocity=-16;
				}


			}

			//düşmanı hareket ettir
			for(int i=0; i<numberOfEnemy;i++){

				// düşman ekrandan çıkınca tekrar içeri al
				if(enemyX[i] < -enemy1.getWidth()){

					enemyX[i] = enemyX[i]+numberOfEnemy*distance;

					/*randomNum=rand.nextFloat();
					enemyOffSet[i] = ((rand.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-200));
					enemyOffSet2[i] = ((rand.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-200));
					enemyOffSet3[i] = ((rand.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-200));*/
					yNumbers = yEkseni();
					enemyOffSet[i] = (yNumbers[0]);
					enemyOffSet2[i] = (yNumbers[1]);
					enemyOffSet3[i] = (yNumbers[2]);
				}
				else{
					// eğer ekranın içindeyse enemyVelocity kadar sola hareket ettir (sürekli)
					enemyX[i] -= enemyVelocity;

				}

				//oyun başlayınca düşmanları getir
				batch.draw(enemy1,enemyX[i],enemyOffSet[i],Gdx.graphics.getWidth()/15,Gdx.graphics.getHeight()/10);
				batch.draw(enemy2,enemyX[i],enemyOffSet2[i],Gdx.graphics.getWidth()/15,Gdx.graphics.getHeight()/10);
				batch.draw(enemy3,enemyX[i],enemyOffSet3[i],Gdx.graphics.getWidth()/15,Gdx.graphics.getHeight()/10);

				//System.out.println(deneme[0]+" - "+deneme[1]+" - "+deneme[2]+"@@@@@@@@@@@@@@@@@@");
				enemyCircle[i].set(enemyX[i]+Gdx.graphics.getWidth()/30,enemyOffSet[i]+Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);
				enemyCircle2[i].set(enemyX[i]+Gdx.graphics.getWidth()/30,enemyOffSet2[i]+Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);
				enemyCircle3[i].set(enemyX[i]+Gdx.graphics.getWidth()/30,enemyOffSet3[i]+Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);
			}





			//yer çekimi
			if(shipY>0){
				velocity += gravity;
				shipY -= velocity;
			}else {
				gameState=2;
			}

		}else if (gameState==0) {
			fontGameOver.draw(batch," Click to Play  ",Gdx.graphics.getWidth()/3,Gdx.graphics.getHeight()/3);

			if (Gdx.input.justTouched()){
				gameState=1;// 0-not start  1-start 2-end

			}
		}else if(gameState==2){
			fontGameOver.draw(batch," Click to Play Again ",Gdx.graphics.getWidth()/4,Gdx.graphics.getHeight()/2);

			if (Gdx.input.justTouched()){
				gameState=1;// 0-not start  1-start 2-end
				shipY=Gdx.graphics.getHeight() / 2;
			}


			for(int i=0; i<numberOfEnemy;i++){
				enemyX[i] = Gdx.graphics.getWidth()-ship.getWidth()/2+i*distance;
				/*enemyOffSet[i] = ((rand.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-200));
				enemyOffSet2[i] = ((rand.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-200));
				enemyOffSet3[i] = ((rand.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-200));*/

				yNumbers = yEkseni();
				enemyOffSet[i] = (yNumbers[0]);
				enemyOffSet2[i] = (yNumbers[1]);
				enemyOffSet3[i] = (yNumbers[2]);

				enemyCircle[i] = new Circle();
				enemyCircle2[i] = new Circle();
				enemyCircle3[i] = new Circle();

			}
			velocity=0;
			scoredEnemy=0;
			score=0;
			enemyVelocity=10;



		}


		fontScore.draw(batch,"Score: "+String.valueOf(score),100,Gdx.graphics.getHeight()-Gdx.graphics.getHeight()/10);
		batch.draw(ship,shipX,shipY,Gdx.graphics.getWidth()/15,Gdx.graphics.getHeight()/10);
		batch.end();

		//Çemberleri çizdiriyoruz DRAW CİRCLİES
		shipCircle.set(shipX+Gdx.graphics.getWidth()/30,shipY+Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);


		for(int i=0;i<numberOfEnemy;i++){

			if(Intersector.overlaps(shipCircle,enemyCircle[i]) ||
					Intersector.overlaps(shipCircle,enemyCircle2[i]) ||
					Intersector.overlaps(shipCircle,enemyCircle3[i]) ){

				//Çarpışma olduğunda
				gameState=2;


			}


		}
		//shapeRenderer.end();



	}
	public float[] yEkseni(){
		float[] yEksenNumbers = new float[3];

		float number=0;
		float number2=0;
		float number3=0;
		float boy =Gdx.graphics.getHeight()/10;
		//en üst ile en alt arası sayılar
		number = rand.nextInt(Gdx.graphics.getHeight()-Gdx.graphics.getHeight()/10-Gdx.graphics.getHeight()/10+1)+Gdx.graphics.getHeight()/10;
		//2 tane sayı birbirine yakın olacak hep
		//1 tane sayı bu 2'linin altına veya üstüne gidecek
		// EN ALT =  0+Gdx.graphics.getHeight()/10
		// EN ÜST Gdx.graphics.getHeight()-Gdx.graphics.getHeight()/10;

		//Üst sınırı geçmiyorsa üstte geçiyorsa altta
		if(number+200>=Gdx.graphics.getHeight()-Gdx.graphics.getHeight()/10){
			number2=number-150;
		}else {
			number2=number+150;
		}


		// adamın geçeceği boşluk
		if(number2>number){

			if(number2+300>Gdx.graphics.getHeight()-Gdx.graphics.getHeight()/10){
				number3=number-boy*5;
			}
			else {
				number3=number2+boy*(3.2f);
			}
		}
		else{
			number3=number2-boy*4;
		}

		yEksenNumbers[0]=number;
		yEksenNumbers[1]=number2;
		yEksenNumbers[2]=number3;


		return yEksenNumbers;
	}
	
	@Override
	public void dispose () {

	}
}
