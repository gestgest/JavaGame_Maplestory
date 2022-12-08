package data;

import javax.swing.ImageIcon;

public class User {
	private String name;

	private int hp;
	private int x,y; //실제론 10000, 10000 => 100,100
	//여러개의 이미지
	// 왼쪽0
	// 오른쪽1
	// 왼쪽 걷기 2 3 4 5
	// 오른쪽 걷기 6 7 8 9
	// 왼 쩜 10
	// 오른 쩜 11
	// 왼 공 12 13 14
	// 오른 공 15 16 17
	private ImageIcon image[];
	private int keybuff;
	private int degree;
	private int damagedTime;
	private boolean isLeft;
	private boolean isJump;
	private boolean isWalk;
	private boolean isAttack;
	private boolean isDamaged;
	private long walkStart;
	private long walkTime;
	private long attackStart;
	private long attackTime;
	private long damagedStart;
	private int velocity;
	private int type;
	
	
	//없앨 내용 [로그인을 처음 했다면]
	public User(String name,int type, int x, int y)
	{
		this.name = name;
		this.x = x;
		this.y = y;
		this.type = type;
		
		init_User();
	}
	public User(String username)
	{
		this.name = username;
		init_User();
	}
	
	public User(User user)
	{
		this.name = user.name;
		this.x = user.x;
		this.y = user.y;
		
	}
	
	private void init_User()
	{
		//Init함수로 처리하든 해야함 
		this.isLeft = true;
		this.isJump = false;
		this.isWalk = false;
		this.isAttack = false;
		this.walkStart = 0;
		this.walkTime = 0;
		this.velocity = 0;
		this.keybuff = 0;
		this.degree = 0;
		this.hp = 100;
		

		int i = 0;
		ImageIcon [][] userImageIcons = new ImageIcon[3][];
		
		userImageIcons[i] = new ImageIcon[18];
		userImageIcons[i][0] = new ImageIcon("src/res/img/character/warrior/IdleLeft.png");
		userImageIcons[i][1] = new ImageIcon("src/res/img/character/warrior/IdleRight.png");
		userImageIcons[i][2] = new ImageIcon("src/res/img/character/warrior/WalkLeft1.png");
		userImageIcons[i][3] = new ImageIcon("src/res/img/character/warrior/WalkLeft2.png");
		userImageIcons[i][4] = new ImageIcon("src/res/img/character/warrior/WalkLeft3.png");
		userImageIcons[i][5] = new ImageIcon("src/res/img/character/warrior/WalkLeft4.png");
		userImageIcons[i][6] = new ImageIcon("src/res/img/character/warrior/WalkRight1.png");
		userImageIcons[i][7] = new ImageIcon("src/res/img/character/warrior/WalkRight2.png");
		userImageIcons[i][8] = new ImageIcon("src/res/img/character/warrior/WalkRight3.png");
		userImageIcons[i][9] = new ImageIcon("src/res/img/character/warrior/WalkRight4.png");
		userImageIcons[i][10] = new ImageIcon("src/res/img/character/warrior/JumpLeft.png");
		userImageIcons[i][11] = new ImageIcon("src/res/img/character/warrior/JumpRight.png");
		userImageIcons[i][12] = new ImageIcon("src/res/img/character/warrior/AttackLeft1.png");
		userImageIcons[i][13] = new ImageIcon("src/res/img/character/warrior/AttackLeft2.png");
		userImageIcons[i][14] = new ImageIcon("src/res/img/character/warrior/AttackLeft3.png");
		userImageIcons[i][15] = new ImageIcon("src/res/img/character/warrior/AttackRight1.png");
		userImageIcons[i][16] = new ImageIcon("src/res/img/character/warrior/AttackRight2.png");
		userImageIcons[i][17] = new ImageIcon("src/res/img/character/warrior/AttackRight3.png");
		

		this.image = new ImageIcon[18];
		for(i = 0; i < 18; i++)
		{
			this.image[i] = userImageIcons[0][i];
		}
		
	}

	public String getName() {return name; }
	public void setName(String name) {this.name = name; }
	
	public int getX() { return x; }
	public int getY() { return y; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
	public void setX(String x) { this.x = Integer.parseInt(x); }
	public void setY(String y) { this.y = Integer.parseInt(y); }

	public int getKeybuff() { return keybuff; }
	public void setKeybuff(int keybuff) { this.keybuff = keybuff; }
	public int getHP() { return hp; }
	public void setHP(int hp) { this.hp = hp; }
	public int getType() { return type; }
	public void setType(int type) { this.type = type; }

	public int getDegree() { return degree; }
	public void setDegree(int degree) { this.degree = degree; }
	public int getDamagedTime() { return damagedTime; }
	public void setDamagedTime(int damagedTime) { this.damagedTime = damagedTime; }
	
	public boolean getIsLeft() { return isLeft; }
	public void setIsLeft(boolean isLeft) { this.isLeft = isLeft; }
	public boolean getIsJump() { return isJump; }
	public void setIsJump(boolean isJump) { this.isJump = isJump; }
	public boolean getIsWalk() { return isWalk; }
	public void setIsWalk(boolean isWalk) { this.isWalk = isWalk; }
	public boolean getIsAttack() { return isAttack; }
	public void setIsAttack(boolean isAttack) { this.isAttack = isAttack; }
	public boolean getIsDamaged() { return isDamaged; }
	public void setIsDamaged(boolean isDamaged) { this.isDamaged = isDamaged; }
	
	
	public int getVelocity() { return velocity; }
	public void setVelocity(int velocity) { this.velocity = velocity; }

	public long getWalkStart() { return walkStart; }
	public void setWalkStart(long walkStart) { this.walkStart = walkStart; }
	public long getWalkTime() { return walkTime; }
	public void setWalkTime(long walkTime) { this.walkTime = walkTime; }
	public long getAttackStart() { return attackStart; }
	public void setAttackStart(long attackStart) { this.attackStart = attackStart; }
	public long getAttackTime() { return attackTime; }
	public void setAttackTime(long attackTime) { this.attackTime = attackTime; }
	public long getDamagedStart() { return damagedStart; }
	public void setDamagedStart(long damagedStart) { this.damagedStart = damagedStart; }
	
	
	//index : 0 : leftidle
	//1 : rightidle
	public ImageIcon getImg(int index) {
		if(image.length <= index)
			return image[0];
		return image[index];
	}
	
	
	public void setImg(ImageIcon image, int index) {
		this.image[index] = image;
	}
	
	//직업
	
	//hp
	//마나
	//경험치
	//공격력
	//
	
	
}
