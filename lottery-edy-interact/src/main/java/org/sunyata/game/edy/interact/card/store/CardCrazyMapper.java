/*
 *
 *
 *  * Copyright (c) 2017 Leo Lee(lichl.1980@163.com).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy
 *  * of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *  *
 *
 */

package org.sunyata.game.edy.interact.card.store;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.sunyata.game.edy.interact.card.model.CrazyCard;

/**
 * Created by leo on 17/3/20.
 */
@Mapper
public interface CardCrazyMapper {
    @Select("SELECT * FROM crazy_cards WHERE prize_level = #{prizeLevel} Limit #{random},1")
    CrazyCard getCards(@Param("prizeLevel") int prizeLevel, @Param("random") int random);


    @Select("SELECT * FROM crazy_cards WHERE id = #{id}")
    CrazyCard getCardsById(@Param("id") String id);

    @Select("SELECT count(*) FROM crazy_cards WHERE prize_level = #{prizeLevel}")
    Integer getCardsCount(@Param("prizeLevel") int prizeLevel);

}