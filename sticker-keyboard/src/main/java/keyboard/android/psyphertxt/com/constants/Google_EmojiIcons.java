package keyboard.android.psyphertxt.com.constants;

import keyboard.android.psyphertxt.com.BuildConfig;
import keyboard.android.psyphertxt.com.R;
import keyboard.android.psyphertxt.com.Utility;

public final class Google_EmojiIcons extends EmojiIcons {
    public Google_EmojiIcons(){

        if(BuildConfig.APPLICATION_ID.contains("gtokkeyboard")){
            cyfaStickerIconIds = Utility.initArrayList(
                    R.drawable.abi_you_know, R.drawable.abin_dada, R.drawable.agye_gon, R.drawable.ashe_wu_roff, R.drawable.away_bus,
                    R.drawable.ay_2, R.drawable.ay3_late, R.drawable.cant_think_far, R.drawable.chale, R.drawable.chop_kiss, R.drawable.dey_gee,
                    R.drawable.e_go_bee, R.drawable.eish, R.drawable.enko_yie, R.drawable.forgeti_obia, R.drawable.girls_abr3,
                    R.drawable.gyama_wo_yale, R.drawable.gyama_wo_yale_ama, R.drawable.gye_wu_twoo, R.drawable.herh_chai, R.drawable.hug, R.drawable.i_cant_think_madness, R.drawable.i_laugh_enter,
                    R.drawable.i_love_you, R.drawable.i_miss_u, R.drawable.kpuu_kpaa, R.drawable.leg_over, R.drawable.ma_da_koraa, R.drawable.man_tire,
                    R.drawable.marry_me, R.drawable.medo_wo, R.drawable.mtchewww, R.drawable.never_hex, R.drawable.no_bf, R.drawable.no_ko_fioo,
                    R.drawable.oh_ho, R.drawable.oh_ok, R.drawable.ohemaa, R.drawable.opana, R.drawable.say_fi, R.drawable.see_your_life,
                    R.drawable.send_me_mobile_money, R.drawable.tenkew, R.drawable.too_known, R.drawable.tu_gu_me_su, R.drawable.u_pap, R.drawable.wha_less,
                    R.drawable.who_said_tweaa, R.drawable.wo_no_no, R.drawable.wu_be_ti_kpa, R.drawable.wu_y3_nam_paa, R.drawable.y3_wo_krom, R.drawable.you_do_all,
                    R.drawable.you_dat
            );
        }else {
            cyfaStickerIconIds = Utility.initArrayList(
                    R.drawable.abi_you_know, R.drawable.abin_dada, R.drawable.agye_gon, R.drawable.ashe_wu_roff, R.drawable.away_bus,
                    R.drawable.ay, R.drawable.ay3_late, R.drawable.cant_think_far, R.drawable.chop_kiss, R.drawable.dey_gee,
                    R.drawable.e_go_bee, R.drawable.eish, R.drawable.enko_yie, R.drawable.forgeti_obia, R.drawable.girls_abr3,
                    R.drawable.gye_wu_twoo, R.drawable.heart, R.drawable.herh_chai, R.drawable.i_cant_think_madness, R.drawable.i_laugh_enter,
                    R.drawable.kpuu_kpaa, R.drawable.laugh, R.drawable.leg_over, R.drawable.ma_da_koraa, R.drawable.man_tire,
                    R.drawable.medo_wo, R.drawable.mobile_money, R.drawable.mtchewww, R.drawable.never_hex, R.drawable.no_ko_fioo,
                    R.drawable.oh_ho, R.drawable.ohemaa, R.drawable.opana, R.drawable.proposal_sized, R.drawable.see_your_life,
                    R.drawable.tenkew, R.drawable.too_known, R.drawable.tu_gu_me_su, R.drawable.u_pap, R.drawable.wha_less,
                    R.drawable.wo_no_no, R.drawable.wu_be_ti_kpa, R.drawable.wu_y3_nam_paa, R.drawable.you_do_all
            );
        }
    }
}