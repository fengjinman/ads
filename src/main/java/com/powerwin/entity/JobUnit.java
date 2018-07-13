package com.powerwin.entity;

/**
 * 任务单位 RMB：人民币 
 * VIP：VIP经验 
 * GOLD：赢取金币数 
 * GOLD2：995棋牌，多个活动赢取金币
 * REDPACKET：是否兑换5元红包（1已兑换，0未兑换） （斗牛大亨） 
 * LEVEL：金牛等级（斗牛大亨） 
 * TOTAL：累积兑换的红包（捕鱼部落）
 * PLAYTIME：在线时长 
 * MIN：在线时长（分钟）
 * REDCOUPON：游戏产出红包卷数量	（麦游斗地主）
 * 金牛等级：充值10元=1等级
 * payamount：充值金额
 * frmb：首充金额（拼花大亨）
 * 
 */
public enum JobUnit {

	RMB("payamount"), VIP("vip"), GOLD("wingold"), GOLD2("wingold2"), REDPACKET(
			"redpacket"), LEVEL("level"), PLAYNUM("playnum"), TOTAL("total"), PLAYTIME(
			"playtime"), MIN("playtime"), REDCOUPON("redcoupon"), TAURUSLEVEL("tauruslevel"), 
			FIRSTRMB("firstamount");

	private String name;

	JobUnit(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

}
