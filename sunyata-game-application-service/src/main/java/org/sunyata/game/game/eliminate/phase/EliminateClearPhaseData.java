package org.sunyata.game.game.eliminate.phase;


import org.sunyata.game.game.PhaseData;

import java.math.BigDecimal;

/**
 * Created by leo on 17/5/16.
 */
public class EliminateClearPhaseData extends PhaseData {

    private String serialNo;

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public EliminateClearPhaseData setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
        return this;
    }

    private BigDecimal totalMoney;//获得的总奖金

    public EliminateClearPhaseData setSerialNo(String serialNo) {
        this.serialNo = serialNo;
        return this;
    }

    public String getSerialNo() {
        return serialNo;
    }
}