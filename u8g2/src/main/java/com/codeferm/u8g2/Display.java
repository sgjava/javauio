/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2;

import static com.codeferm.u8g2.U8g2.U8G2_R0;
import static com.codeferm.u8g2.U8g2.u8x8_arm_linux_gpio_and_delay;
import static com.codeferm.u8g2.U8g2.u8x8_byte_arm_linux_hw_i2c;
import static com.codeferm.u8g2.U8g2.u8x8_byte_arm_linux_hw_spi;
import static com.codeferm.u8g2.U8x8.u8x8_byte_4wire_sw_spi;
import static com.codeferm.u8g2.U8x8.u8x8_byte_sw_i2c;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * Display allows dynamic selection of setup and fonts at run time. This is a monster class because of the generated switch
 * statements.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class Display {

    /**
     * Return font pointer based on enum.
     *
     * @param fontType Font type eunm.
     * @return Font pointer.
     */
    public long getFontPtr(final FontType fontType) {
        long fontSel = 0;
        switch (fontType) {
            case FONT_U8GLIB_4_TF ->
                fontSel = Fonts.u8g2_font_u8glib_4_tf;
            case FONT_U8GLIB_4_TR ->
                fontSel = Fonts.u8g2_font_u8glib_4_tr;
            case FONT_U8GLIB_4_HF ->
                fontSel = Fonts.u8g2_font_u8glib_4_hf;
            case FONT_U8GLIB_4_HR ->
                fontSel = Fonts.u8g2_font_u8glib_4_hr;
            case FONT_M2ICON_5_TF ->
                fontSel = Fonts.u8g2_font_m2icon_5_tf;
            case FONT_M2ICON_7_TF ->
                fontSel = Fonts.u8g2_font_m2icon_7_tf;
            case FONT_M2ICON_9_TF ->
                fontSel = Fonts.u8g2_font_m2icon_9_tf;
            case FONT_EMOTICONS21_TR ->
                fontSel = Fonts.u8g2_font_emoticons21_tr;
            case FONT_BATTERY19_TN ->
                fontSel = Fonts.u8g2_font_battery19_tn;
            case FONT_BATTERY24_TR ->
                fontSel = Fonts.u8g2_font_battery24_tr;
            case FONT_SQUEEZED_R6_TR ->
                fontSel = Fonts.u8g2_font_squeezed_r6_tr;
            case FONT_SQUEEZED_R6_TN ->
                fontSel = Fonts.u8g2_font_squeezed_r6_tn;
            case FONT_SQUEEZED_B6_TR ->
                fontSel = Fonts.u8g2_font_squeezed_b6_tr;
            case FONT_SQUEEZED_B6_TN ->
                fontSel = Fonts.u8g2_font_squeezed_b6_tn;
            case FONT_SQUEEZED_R7_TR ->
                fontSel = Fonts.u8g2_font_squeezed_r7_tr;
            case FONT_SQUEEZED_R7_TN ->
                fontSel = Fonts.u8g2_font_squeezed_r7_tn;
            case FONT_SQUEEZED_B7_TR ->
                fontSel = Fonts.u8g2_font_squeezed_b7_tr;
            case FONT_SQUEEZED_B7_TN ->
                fontSel = Fonts.u8g2_font_squeezed_b7_tn;
            case FONT_PERCENT_CIRCLE_25_HN ->
                fontSel = Fonts.u8g2_font_percent_circle_25_hn;
            case FONT_FREEDOOMR10_TU ->
                fontSel = Fonts.u8g2_font_freedoomr10_tu;
            case FONT_FREEDOOMR10_MU ->
                fontSel = Fonts.u8g2_font_freedoomr10_mu;
            case FONT_FREEDOOMR25_TN ->
                fontSel = Fonts.u8g2_font_freedoomr25_tn;
            case FONT_FREEDOOMR25_MN ->
                fontSel = Fonts.u8g2_font_freedoomr25_mn;
            case FONT_7SEGMENTS_26X42_MN ->
                fontSel = Fonts.u8g2_font_7Segments_26x42_mn;
            case FONT_7_SEG_33X19_MN ->
                fontSel = Fonts.u8g2_font_7_Seg_33x19_mn;
            case FONT_7_SEG_41X21_MN ->
                fontSel = Fonts.u8g2_font_7_Seg_41x21_mn;
            case FONT_TINY5_TF ->
                fontSel = Fonts.u8g2_font_tiny5_tf;
            case FONT_TINY5_TR ->
                fontSel = Fonts.u8g2_font_tiny5_tr;
            case FONT_TINY5_TE ->
                fontSel = Fonts.u8g2_font_tiny5_te;
            case FONT_TINY5_T_ALL ->
                fontSel = Fonts.u8g2_font_tiny5_t_all;
            case FONT_04B_03B_TR ->
                fontSel = Fonts.u8g2_font_04b_03b_tr;
            case FONT_04B_03_TR ->
                fontSel = Fonts.u8g2_font_04b_03_tr;
            case FONT_AMSTRAD_CPC_EXTENDED_8F ->
                fontSel = Fonts.u8g2_font_amstrad_cpc_extended_8f;
            case FONT_AMSTRAD_CPC_EXTENDED_8R ->
                fontSel = Fonts.u8g2_font_amstrad_cpc_extended_8r;
            case FONT_AMSTRAD_CPC_EXTENDED_8N ->
                fontSel = Fonts.u8g2_font_amstrad_cpc_extended_8n;
            case FONT_AMSTRAD_CPC_EXTENDED_8U ->
                fontSel = Fonts.u8g2_font_amstrad_cpc_extended_8u;
            case FONT_CURSOR_TF ->
                fontSel = Fonts.u8g2_font_cursor_tf;
            case FONT_CURSOR_TR ->
                fontSel = Fonts.u8g2_font_cursor_tr;
            case FONT_MICRO_TR ->
                fontSel = Fonts.u8g2_font_micro_tr;
            case FONT_MICRO_TN ->
                fontSel = Fonts.u8g2_font_micro_tn;
            case FONT_MICRO_MR ->
                fontSel = Fonts.u8g2_font_micro_mr;
            case FONT_MICRO_MN ->
                fontSel = Fonts.u8g2_font_micro_mn;
            case FONT_4X6_TF ->
                fontSel = Fonts.u8g2_font_4x6_tf;
            case FONT_4X6_TR ->
                fontSel = Fonts.u8g2_font_4x6_tr;
            case FONT_4X6_TN ->
                fontSel = Fonts.u8g2_font_4x6_tn;
            case FONT_4X6_MF ->
                fontSel = Fonts.u8g2_font_4x6_mf;
            case FONT_4X6_MR ->
                fontSel = Fonts.u8g2_font_4x6_mr;
            case FONT_4X6_MN ->
                fontSel = Fonts.u8g2_font_4x6_mn;
            case FONT_4X6_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_4x6_t_cyrillic;
            case FONT_5X7_TF ->
                fontSel = Fonts.u8g2_font_5x7_tf;
            case FONT_5X7_TR ->
                fontSel = Fonts.u8g2_font_5x7_tr;
            case FONT_5X7_TN ->
                fontSel = Fonts.u8g2_font_5x7_tn;
            case FONT_5X7_MF ->
                fontSel = Fonts.u8g2_font_5x7_mf;
            case FONT_5X7_MR ->
                fontSel = Fonts.u8g2_font_5x7_mr;
            case FONT_5X7_MN ->
                fontSel = Fonts.u8g2_font_5x7_mn;
            case FONT_5X7_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_5x7_t_cyrillic;
            case FONT_5X8_TF ->
                fontSel = Fonts.u8g2_font_5x8_tf;
            case FONT_5X8_TR ->
                fontSel = Fonts.u8g2_font_5x8_tr;
            case FONT_5X8_TN ->
                fontSel = Fonts.u8g2_font_5x8_tn;
            case FONT_5X8_MF ->
                fontSel = Fonts.u8g2_font_5x8_mf;
            case FONT_5X8_MR ->
                fontSel = Fonts.u8g2_font_5x8_mr;
            case FONT_5X8_MN ->
                fontSel = Fonts.u8g2_font_5x8_mn;
            case FONT_5X8_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_5x8_t_cyrillic;
            case FONT_6X10_TF ->
                fontSel = Fonts.u8g2_font_6x10_tf;
            case FONT_6X10_TR ->
                fontSel = Fonts.u8g2_font_6x10_tr;
            case FONT_6X10_TN ->
                fontSel = Fonts.u8g2_font_6x10_tn;
            case FONT_6X10_MF ->
                fontSel = Fonts.u8g2_font_6x10_mf;
            case FONT_6X10_MR ->
                fontSel = Fonts.u8g2_font_6x10_mr;
            case FONT_6X10_MN ->
                fontSel = Fonts.u8g2_font_6x10_mn;
            case FONT_6X12_TF ->
                fontSel = Fonts.u8g2_font_6x12_tf;
            case FONT_6X12_TR ->
                fontSel = Fonts.u8g2_font_6x12_tr;
            case FONT_6X12_TN ->
                fontSel = Fonts.u8g2_font_6x12_tn;
            case FONT_6X12_TE ->
                fontSel = Fonts.u8g2_font_6x12_te;
            case FONT_6X12_MF ->
                fontSel = Fonts.u8g2_font_6x12_mf;
            case FONT_6X12_MR ->
                fontSel = Fonts.u8g2_font_6x12_mr;
            case FONT_6X12_MN ->
                fontSel = Fonts.u8g2_font_6x12_mn;
            case FONT_6X12_ME ->
                fontSel = Fonts.u8g2_font_6x12_me;
            case FONT_6X12_T_SYMBOLS ->
                fontSel = Fonts.u8g2_font_6x12_t_symbols;
            case FONT_6X12_M_SYMBOLS ->
                fontSel = Fonts.u8g2_font_6x12_m_symbols;
            case FONT_6X12_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_6x12_t_cyrillic;
            case FONT_6X13_TF ->
                fontSel = Fonts.u8g2_font_6x13_tf;
            case FONT_6X13_TR ->
                fontSel = Fonts.u8g2_font_6x13_tr;
            case FONT_6X13_TN ->
                fontSel = Fonts.u8g2_font_6x13_tn;
            case FONT_6X13_TE ->
                fontSel = Fonts.u8g2_font_6x13_te;
            case FONT_6X13_MF ->
                fontSel = Fonts.u8g2_font_6x13_mf;
            case FONT_6X13_MR ->
                fontSel = Fonts.u8g2_font_6x13_mr;
            case FONT_6X13_MN ->
                fontSel = Fonts.u8g2_font_6x13_mn;
            case FONT_6X13_ME ->
                fontSel = Fonts.u8g2_font_6x13_me;
            case FONT_6X13_T_HEBREW ->
                fontSel = Fonts.u8g2_font_6x13_t_hebrew;
            case FONT_6X13_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_6x13_t_cyrillic;
            case FONT_6X13B_TF ->
                fontSel = Fonts.u8g2_font_6x13B_tf;
            case FONT_6X13B_TR ->
                fontSel = Fonts.u8g2_font_6x13B_tr;
            case FONT_6X13B_TN ->
                fontSel = Fonts.u8g2_font_6x13B_tn;
            case FONT_6X13B_MF ->
                fontSel = Fonts.u8g2_font_6x13B_mf;
            case FONT_6X13B_MR ->
                fontSel = Fonts.u8g2_font_6x13B_mr;
            case FONT_6X13B_MN ->
                fontSel = Fonts.u8g2_font_6x13B_mn;
            case FONT_6X13B_T_HEBREW ->
                fontSel = Fonts.u8g2_font_6x13B_t_hebrew;
            case FONT_6X13B_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_6x13B_t_cyrillic;
            case FONT_6X13O_TF ->
                fontSel = Fonts.u8g2_font_6x13O_tf;
            case FONT_6X13O_TR ->
                fontSel = Fonts.u8g2_font_6x13O_tr;
            case FONT_6X13O_TN ->
                fontSel = Fonts.u8g2_font_6x13O_tn;
            case FONT_6X13O_MF ->
                fontSel = Fonts.u8g2_font_6x13O_mf;
            case FONT_6X13O_MR ->
                fontSel = Fonts.u8g2_font_6x13O_mr;
            case FONT_6X13O_MN ->
                fontSel = Fonts.u8g2_font_6x13O_mn;
            case FONT_7X13_TF ->
                fontSel = Fonts.u8g2_font_7x13_tf;
            case FONT_7X13_TR ->
                fontSel = Fonts.u8g2_font_7x13_tr;
            case FONT_7X13_TN ->
                fontSel = Fonts.u8g2_font_7x13_tn;
            case FONT_7X13_TE ->
                fontSel = Fonts.u8g2_font_7x13_te;
            case FONT_7X13_MF ->
                fontSel = Fonts.u8g2_font_7x13_mf;
            case FONT_7X13_MR ->
                fontSel = Fonts.u8g2_font_7x13_mr;
            case FONT_7X13_MN ->
                fontSel = Fonts.u8g2_font_7x13_mn;
            case FONT_7X13_ME ->
                fontSel = Fonts.u8g2_font_7x13_me;
            case FONT_7X13_T_SYMBOLS ->
                fontSel = Fonts.u8g2_font_7x13_t_symbols;
            case FONT_7X13_M_SYMBOLS ->
                fontSel = Fonts.u8g2_font_7x13_m_symbols;
            case FONT_7X13_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_7x13_t_cyrillic;
            case FONT_7X13B_TF ->
                fontSel = Fonts.u8g2_font_7x13B_tf;
            case FONT_7X13B_TR ->
                fontSel = Fonts.u8g2_font_7x13B_tr;
            case FONT_7X13B_TN ->
                fontSel = Fonts.u8g2_font_7x13B_tn;
            case FONT_7X13B_MF ->
                fontSel = Fonts.u8g2_font_7x13B_mf;
            case FONT_7X13B_MR ->
                fontSel = Fonts.u8g2_font_7x13B_mr;
            case FONT_7X13B_MN ->
                fontSel = Fonts.u8g2_font_7x13B_mn;
            case FONT_7X13O_TF ->
                fontSel = Fonts.u8g2_font_7x13O_tf;
            case FONT_7X13O_TR ->
                fontSel = Fonts.u8g2_font_7x13O_tr;
            case FONT_7X13O_TN ->
                fontSel = Fonts.u8g2_font_7x13O_tn;
            case FONT_7X13O_MF ->
                fontSel = Fonts.u8g2_font_7x13O_mf;
            case FONT_7X13O_MR ->
                fontSel = Fonts.u8g2_font_7x13O_mr;
            case FONT_7X13O_MN ->
                fontSel = Fonts.u8g2_font_7x13O_mn;
            case FONT_7X14_TF ->
                fontSel = Fonts.u8g2_font_7x14_tf;
            case FONT_7X14_TR ->
                fontSel = Fonts.u8g2_font_7x14_tr;
            case FONT_7X14_TN ->
                fontSel = Fonts.u8g2_font_7x14_tn;
            case FONT_7X14_MF ->
                fontSel = Fonts.u8g2_font_7x14_mf;
            case FONT_7X14_MR ->
                fontSel = Fonts.u8g2_font_7x14_mr;
            case FONT_7X14_MN ->
                fontSel = Fonts.u8g2_font_7x14_mn;
            case FONT_7X14B_TF ->
                fontSel = Fonts.u8g2_font_7x14B_tf;
            case FONT_7X14B_TR ->
                fontSel = Fonts.u8g2_font_7x14B_tr;
            case FONT_7X14B_TN ->
                fontSel = Fonts.u8g2_font_7x14B_tn;
            case FONT_7X14B_MF ->
                fontSel = Fonts.u8g2_font_7x14B_mf;
            case FONT_7X14B_MR ->
                fontSel = Fonts.u8g2_font_7x14B_mr;
            case FONT_7X14B_MN ->
                fontSel = Fonts.u8g2_font_7x14B_mn;
            case FONT_8X13_TF ->
                fontSel = Fonts.u8g2_font_8x13_tf;
            case FONT_8X13_TR ->
                fontSel = Fonts.u8g2_font_8x13_tr;
            case FONT_8X13_TN ->
                fontSel = Fonts.u8g2_font_8x13_tn;
            case FONT_8X13_TE ->
                fontSel = Fonts.u8g2_font_8x13_te;
            case FONT_8X13_MF ->
                fontSel = Fonts.u8g2_font_8x13_mf;
            case FONT_8X13_MR ->
                fontSel = Fonts.u8g2_font_8x13_mr;
            case FONT_8X13_MN ->
                fontSel = Fonts.u8g2_font_8x13_mn;
            case FONT_8X13_ME ->
                fontSel = Fonts.u8g2_font_8x13_me;
            case FONT_8X13_T_SYMBOLS ->
                fontSel = Fonts.u8g2_font_8x13_t_symbols;
            case FONT_8X13_M_SYMBOLS ->
                fontSel = Fonts.u8g2_font_8x13_m_symbols;
            case FONT_8X13_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_8x13_t_cyrillic;
            case FONT_8X13B_TF ->
                fontSel = Fonts.u8g2_font_8x13B_tf;
            case FONT_8X13B_TR ->
                fontSel = Fonts.u8g2_font_8x13B_tr;
            case FONT_8X13B_TN ->
                fontSel = Fonts.u8g2_font_8x13B_tn;
            case FONT_8X13B_MF ->
                fontSel = Fonts.u8g2_font_8x13B_mf;
            case FONT_8X13B_MR ->
                fontSel = Fonts.u8g2_font_8x13B_mr;
            case FONT_8X13B_MN ->
                fontSel = Fonts.u8g2_font_8x13B_mn;
            case FONT_8X13O_TF ->
                fontSel = Fonts.u8g2_font_8x13O_tf;
            case FONT_8X13O_TR ->
                fontSel = Fonts.u8g2_font_8x13O_tr;
            case FONT_8X13O_TN ->
                fontSel = Fonts.u8g2_font_8x13O_tn;
            case FONT_8X13O_MF ->
                fontSel = Fonts.u8g2_font_8x13O_mf;
            case FONT_8X13O_MR ->
                fontSel = Fonts.u8g2_font_8x13O_mr;
            case FONT_8X13O_MN ->
                fontSel = Fonts.u8g2_font_8x13O_mn;
            case FONT_9X15_TF ->
                fontSel = Fonts.u8g2_font_9x15_tf;
            case FONT_9X15_TR ->
                fontSel = Fonts.u8g2_font_9x15_tr;
            case FONT_9X15_TN ->
                fontSel = Fonts.u8g2_font_9x15_tn;
            case FONT_9X15_TE ->
                fontSel = Fonts.u8g2_font_9x15_te;
            case FONT_9X15_MF ->
                fontSel = Fonts.u8g2_font_9x15_mf;
            case FONT_9X15_MR ->
                fontSel = Fonts.u8g2_font_9x15_mr;
            case FONT_9X15_MN ->
                fontSel = Fonts.u8g2_font_9x15_mn;
            case FONT_9X15_ME ->
                fontSel = Fonts.u8g2_font_9x15_me;
            case FONT_9X15_T_SYMBOLS ->
                fontSel = Fonts.u8g2_font_9x15_t_symbols;
            case FONT_9X15_M_SYMBOLS ->
                fontSel = Fonts.u8g2_font_9x15_m_symbols;
            case FONT_9X15_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_9x15_t_cyrillic;
            case FONT_9X15B_TF ->
                fontSel = Fonts.u8g2_font_9x15B_tf;
            case FONT_9X15B_TR ->
                fontSel = Fonts.u8g2_font_9x15B_tr;
            case FONT_9X15B_TN ->
                fontSel = Fonts.u8g2_font_9x15B_tn;
            case FONT_9X15B_MF ->
                fontSel = Fonts.u8g2_font_9x15B_mf;
            case FONT_9X15B_MR ->
                fontSel = Fonts.u8g2_font_9x15B_mr;
            case FONT_9X15B_MN ->
                fontSel = Fonts.u8g2_font_9x15B_mn;
            case FONT_9X18_TF ->
                fontSel = Fonts.u8g2_font_9x18_tf;
            case FONT_9X18_TR ->
                fontSel = Fonts.u8g2_font_9x18_tr;
            case FONT_9X18_TN ->
                fontSel = Fonts.u8g2_font_9x18_tn;
            case FONT_9X18_MF ->
                fontSel = Fonts.u8g2_font_9x18_mf;
            case FONT_9X18_MR ->
                fontSel = Fonts.u8g2_font_9x18_mr;
            case FONT_9X18_MN ->
                fontSel = Fonts.u8g2_font_9x18_mn;
            case FONT_9X18B_TF ->
                fontSel = Fonts.u8g2_font_9x18B_tf;
            case FONT_9X18B_TR ->
                fontSel = Fonts.u8g2_font_9x18B_tr;
            case FONT_9X18B_TN ->
                fontSel = Fonts.u8g2_font_9x18B_tn;
            case FONT_9X18B_MF ->
                fontSel = Fonts.u8g2_font_9x18B_mf;
            case FONT_9X18B_MR ->
                fontSel = Fonts.u8g2_font_9x18B_mr;
            case FONT_9X18B_MN ->
                fontSel = Fonts.u8g2_font_9x18B_mn;
            case FONT_10X20_TF ->
                fontSel = Fonts.u8g2_font_10x20_tf;
            case FONT_10X20_TR ->
                fontSel = Fonts.u8g2_font_10x20_tr;
            case FONT_10X20_TN ->
                fontSel = Fonts.u8g2_font_10x20_tn;
            case FONT_10X20_TE ->
                fontSel = Fonts.u8g2_font_10x20_te;
            case FONT_10X20_MF ->
                fontSel = Fonts.u8g2_font_10x20_mf;
            case FONT_10X20_MR ->
                fontSel = Fonts.u8g2_font_10x20_mr;
            case FONT_10X20_MN ->
                fontSel = Fonts.u8g2_font_10x20_mn;
            case FONT_10X20_ME ->
                fontSel = Fonts.u8g2_font_10x20_me;
            case FONT_10X20_T_GREEK ->
                fontSel = Fonts.u8g2_font_10x20_t_greek;
            case FONT_10X20_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_10x20_t_cyrillic;
            case FONT_10X20_T_ARABIC ->
                fontSel = Fonts.u8g2_font_10x20_t_arabic;
            case FONT_SIJI_T_6X10 ->
                fontSel = Fonts.u8g2_font_siji_t_6x10;
            case FONT_WAFFLE_T_ALL ->
                fontSel = Fonts.u8g2_font_waffle_t_all;
            case FONT_TOM_THUMB_4X6_T_ALL ->
                fontSel = Fonts.u8g2_font_tom_thumb_4x6_t_all;
            case FONT_TOM_THUMB_4X6_TF ->
                fontSel = Fonts.u8g2_font_tom_thumb_4x6_tf;
            case FONT_TOM_THUMB_4X6_TR ->
                fontSel = Fonts.u8g2_font_tom_thumb_4x6_tr;
            case FONT_TOM_THUMB_4X6_TN ->
                fontSel = Fonts.u8g2_font_tom_thumb_4x6_tn;
            case FONT_TOM_THUMB_4X6_TE ->
                fontSel = Fonts.u8g2_font_tom_thumb_4x6_te;
            case FONT_TOM_THUMB_4X6_MF ->
                fontSel = Fonts.u8g2_font_tom_thumb_4x6_mf;
            case FONT_TOM_THUMB_4X6_MR ->
                fontSel = Fonts.u8g2_font_tom_thumb_4x6_mr;
            case FONT_TOM_THUMB_4X6_MN ->
                fontSel = Fonts.u8g2_font_tom_thumb_4x6_mn;
            case FONT_TOM_THUMB_4X6_ME ->
                fontSel = Fonts.u8g2_font_tom_thumb_4x6_me;
            case FONT_MYSTERY_QUEST_24_TF ->
                fontSel = Fonts.u8g2_font_mystery_quest_24_tf;
            case FONT_MYSTERY_QUEST_24_TR ->
                fontSel = Fonts.u8g2_font_mystery_quest_24_tr;
            case FONT_MYSTERY_QUEST_24_TN ->
                fontSel = Fonts.u8g2_font_mystery_quest_24_tn;
            case FONT_MYSTERY_QUEST_28_TF ->
                fontSel = Fonts.u8g2_font_mystery_quest_28_tf;
            case FONT_MYSTERY_QUEST_28_TR ->
                fontSel = Fonts.u8g2_font_mystery_quest_28_tr;
            case FONT_MYSTERY_QUEST_28_TN ->
                fontSel = Fonts.u8g2_font_mystery_quest_28_tn;
            case FONT_MYSTERY_QUEST_32_TR ->
                fontSel = Fonts.u8g2_font_mystery_quest_32_tr;
            case FONT_MYSTERY_QUEST_32_TN ->
                fontSel = Fonts.u8g2_font_mystery_quest_32_tn;
            case FONT_MYSTERY_QUEST_36_TN ->
                fontSel = Fonts.u8g2_font_mystery_quest_36_tn;
            case FONT_MYSTERY_QUEST_42_TN ->
                fontSel = Fonts.u8g2_font_mystery_quest_42_tn;
            case FONT_MYSTERY_QUEST_48_TN ->
                fontSel = Fonts.u8g2_font_mystery_quest_48_tn;
            case FONT_MYSTERY_QUEST_56_TN ->
                fontSel = Fonts.u8g2_font_mystery_quest_56_tn;
            case FONT_T0_11_TF ->
                fontSel = Fonts.u8g2_font_t0_11_tf;
            case FONT_T0_11_TR ->
                fontSel = Fonts.u8g2_font_t0_11_tr;
            case FONT_T0_11_TN ->
                fontSel = Fonts.u8g2_font_t0_11_tn;
            case FONT_T0_11_TE ->
                fontSel = Fonts.u8g2_font_t0_11_te;
            case FONT_T0_11_MF ->
                fontSel = Fonts.u8g2_font_t0_11_mf;
            case FONT_T0_11_MR ->
                fontSel = Fonts.u8g2_font_t0_11_mr;
            case FONT_T0_11_MN ->
                fontSel = Fonts.u8g2_font_t0_11_mn;
            case FONT_T0_11_ME ->
                fontSel = Fonts.u8g2_font_t0_11_me;
            case FONT_T0_11_T_ALL ->
                fontSel = Fonts.u8g2_font_t0_11_t_all;
            case FONT_T0_11_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_t0_11_t_symbol;
            case FONT_T0_11B_TF ->
                fontSel = Fonts.u8g2_font_t0_11b_tf;
            case FONT_T0_11B_TR ->
                fontSel = Fonts.u8g2_font_t0_11b_tr;
            case FONT_T0_11B_TN ->
                fontSel = Fonts.u8g2_font_t0_11b_tn;
            case FONT_T0_11B_TE ->
                fontSel = Fonts.u8g2_font_t0_11b_te;
            case FONT_T0_11B_MF ->
                fontSel = Fonts.u8g2_font_t0_11b_mf;
            case FONT_T0_11B_MR ->
                fontSel = Fonts.u8g2_font_t0_11b_mr;
            case FONT_T0_11B_MN ->
                fontSel = Fonts.u8g2_font_t0_11b_mn;
            case FONT_T0_11B_ME ->
                fontSel = Fonts.u8g2_font_t0_11b_me;
            case FONT_T0_12_TF ->
                fontSel = Fonts.u8g2_font_t0_12_tf;
            case FONT_T0_12_TR ->
                fontSel = Fonts.u8g2_font_t0_12_tr;
            case FONT_T0_12_TN ->
                fontSel = Fonts.u8g2_font_t0_12_tn;
            case FONT_T0_12_TE ->
                fontSel = Fonts.u8g2_font_t0_12_te;
            case FONT_T0_12_MF ->
                fontSel = Fonts.u8g2_font_t0_12_mf;
            case FONT_T0_12_MR ->
                fontSel = Fonts.u8g2_font_t0_12_mr;
            case FONT_T0_12_MN ->
                fontSel = Fonts.u8g2_font_t0_12_mn;
            case FONT_T0_12_ME ->
                fontSel = Fonts.u8g2_font_t0_12_me;
            case FONT_T0_12_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_t0_12_t_symbol;
            case FONT_T0_12B_TF ->
                fontSel = Fonts.u8g2_font_t0_12b_tf;
            case FONT_T0_12B_TR ->
                fontSel = Fonts.u8g2_font_t0_12b_tr;
            case FONT_T0_12B_TN ->
                fontSel = Fonts.u8g2_font_t0_12b_tn;
            case FONT_T0_12B_TE ->
                fontSel = Fonts.u8g2_font_t0_12b_te;
            case FONT_T0_12B_MF ->
                fontSel = Fonts.u8g2_font_t0_12b_mf;
            case FONT_T0_12B_MR ->
                fontSel = Fonts.u8g2_font_t0_12b_mr;
            case FONT_T0_12B_MN ->
                fontSel = Fonts.u8g2_font_t0_12b_mn;
            case FONT_T0_12B_ME ->
                fontSel = Fonts.u8g2_font_t0_12b_me;
            case FONT_T0_13_TF ->
                fontSel = Fonts.u8g2_font_t0_13_tf;
            case FONT_T0_13_TR ->
                fontSel = Fonts.u8g2_font_t0_13_tr;
            case FONT_T0_13_TN ->
                fontSel = Fonts.u8g2_font_t0_13_tn;
            case FONT_T0_13_TE ->
                fontSel = Fonts.u8g2_font_t0_13_te;
            case FONT_T0_13_MF ->
                fontSel = Fonts.u8g2_font_t0_13_mf;
            case FONT_T0_13_MR ->
                fontSel = Fonts.u8g2_font_t0_13_mr;
            case FONT_T0_13_MN ->
                fontSel = Fonts.u8g2_font_t0_13_mn;
            case FONT_T0_13_ME ->
                fontSel = Fonts.u8g2_font_t0_13_me;
            case FONT_T0_13_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_t0_13_t_symbol;
            case FONT_T0_13B_TF ->
                fontSel = Fonts.u8g2_font_t0_13b_tf;
            case FONT_T0_13B_TR ->
                fontSel = Fonts.u8g2_font_t0_13b_tr;
            case FONT_T0_13B_TN ->
                fontSel = Fonts.u8g2_font_t0_13b_tn;
            case FONT_T0_13B_TE ->
                fontSel = Fonts.u8g2_font_t0_13b_te;
            case FONT_T0_13B_MF ->
                fontSel = Fonts.u8g2_font_t0_13b_mf;
            case FONT_T0_13B_MR ->
                fontSel = Fonts.u8g2_font_t0_13b_mr;
            case FONT_T0_13B_MN ->
                fontSel = Fonts.u8g2_font_t0_13b_mn;
            case FONT_T0_13B_ME ->
                fontSel = Fonts.u8g2_font_t0_13b_me;
            case FONT_T0_14_TF ->
                fontSel = Fonts.u8g2_font_t0_14_tf;
            case FONT_T0_14_TR ->
                fontSel = Fonts.u8g2_font_t0_14_tr;
            case FONT_T0_14_TN ->
                fontSel = Fonts.u8g2_font_t0_14_tn;
            case FONT_T0_14_TE ->
                fontSel = Fonts.u8g2_font_t0_14_te;
            case FONT_T0_14_MF ->
                fontSel = Fonts.u8g2_font_t0_14_mf;
            case FONT_T0_14_MR ->
                fontSel = Fonts.u8g2_font_t0_14_mr;
            case FONT_T0_14_MN ->
                fontSel = Fonts.u8g2_font_t0_14_mn;
            case FONT_T0_14_ME ->
                fontSel = Fonts.u8g2_font_t0_14_me;
            case FONT_T0_14_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_t0_14_t_symbol;
            case FONT_T0_14B_TF ->
                fontSel = Fonts.u8g2_font_t0_14b_tf;
            case FONT_T0_14B_TR ->
                fontSel = Fonts.u8g2_font_t0_14b_tr;
            case FONT_T0_14B_TN ->
                fontSel = Fonts.u8g2_font_t0_14b_tn;
            case FONT_T0_14B_TE ->
                fontSel = Fonts.u8g2_font_t0_14b_te;
            case FONT_T0_14B_MF ->
                fontSel = Fonts.u8g2_font_t0_14b_mf;
            case FONT_T0_14B_MR ->
                fontSel = Fonts.u8g2_font_t0_14b_mr;
            case FONT_T0_14B_MN ->
                fontSel = Fonts.u8g2_font_t0_14b_mn;
            case FONT_T0_14B_ME ->
                fontSel = Fonts.u8g2_font_t0_14b_me;
            case FONT_T0_15_TF ->
                fontSel = Fonts.u8g2_font_t0_15_tf;
            case FONT_T0_15_TR ->
                fontSel = Fonts.u8g2_font_t0_15_tr;
            case FONT_T0_15_TN ->
                fontSel = Fonts.u8g2_font_t0_15_tn;
            case FONT_T0_15_TE ->
                fontSel = Fonts.u8g2_font_t0_15_te;
            case FONT_T0_15_MF ->
                fontSel = Fonts.u8g2_font_t0_15_mf;
            case FONT_T0_15_MR ->
                fontSel = Fonts.u8g2_font_t0_15_mr;
            case FONT_T0_15_MN ->
                fontSel = Fonts.u8g2_font_t0_15_mn;
            case FONT_T0_15_ME ->
                fontSel = Fonts.u8g2_font_t0_15_me;
            case FONT_T0_15_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_t0_15_t_symbol;
            case FONT_T0_15B_TF ->
                fontSel = Fonts.u8g2_font_t0_15b_tf;
            case FONT_T0_15B_TR ->
                fontSel = Fonts.u8g2_font_t0_15b_tr;
            case FONT_T0_15B_TN ->
                fontSel = Fonts.u8g2_font_t0_15b_tn;
            case FONT_T0_15B_TE ->
                fontSel = Fonts.u8g2_font_t0_15b_te;
            case FONT_T0_15B_MF ->
                fontSel = Fonts.u8g2_font_t0_15b_mf;
            case FONT_T0_15B_MR ->
                fontSel = Fonts.u8g2_font_t0_15b_mr;
            case FONT_T0_15B_MN ->
                fontSel = Fonts.u8g2_font_t0_15b_mn;
            case FONT_T0_15B_ME ->
                fontSel = Fonts.u8g2_font_t0_15b_me;
            case FONT_T0_16_TF ->
                fontSel = Fonts.u8g2_font_t0_16_tf;
            case FONT_T0_16_TR ->
                fontSel = Fonts.u8g2_font_t0_16_tr;
            case FONT_T0_16_TN ->
                fontSel = Fonts.u8g2_font_t0_16_tn;
            case FONT_T0_16_TE ->
                fontSel = Fonts.u8g2_font_t0_16_te;
            case FONT_T0_16_MF ->
                fontSel = Fonts.u8g2_font_t0_16_mf;
            case FONT_T0_16_MR ->
                fontSel = Fonts.u8g2_font_t0_16_mr;
            case FONT_T0_16_MN ->
                fontSel = Fonts.u8g2_font_t0_16_mn;
            case FONT_T0_16_ME ->
                fontSel = Fonts.u8g2_font_t0_16_me;
            case FONT_T0_16_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_t0_16_t_symbol;
            case FONT_T0_16B_TF ->
                fontSel = Fonts.u8g2_font_t0_16b_tf;
            case FONT_T0_16B_TR ->
                fontSel = Fonts.u8g2_font_t0_16b_tr;
            case FONT_T0_16B_TN ->
                fontSel = Fonts.u8g2_font_t0_16b_tn;
            case FONT_T0_16B_TE ->
                fontSel = Fonts.u8g2_font_t0_16b_te;
            case FONT_T0_16B_MF ->
                fontSel = Fonts.u8g2_font_t0_16b_mf;
            case FONT_T0_16B_MR ->
                fontSel = Fonts.u8g2_font_t0_16b_mr;
            case FONT_T0_16B_MN ->
                fontSel = Fonts.u8g2_font_t0_16b_mn;
            case FONT_T0_16B_ME ->
                fontSel = Fonts.u8g2_font_t0_16b_me;
            case FONT_T0_17_TF ->
                fontSel = Fonts.u8g2_font_t0_17_tf;
            case FONT_T0_17_TR ->
                fontSel = Fonts.u8g2_font_t0_17_tr;
            case FONT_T0_17_TN ->
                fontSel = Fonts.u8g2_font_t0_17_tn;
            case FONT_T0_17_TE ->
                fontSel = Fonts.u8g2_font_t0_17_te;
            case FONT_T0_17_MF ->
                fontSel = Fonts.u8g2_font_t0_17_mf;
            case FONT_T0_17_MR ->
                fontSel = Fonts.u8g2_font_t0_17_mr;
            case FONT_T0_17_MN ->
                fontSel = Fonts.u8g2_font_t0_17_mn;
            case FONT_T0_17_ME ->
                fontSel = Fonts.u8g2_font_t0_17_me;
            case FONT_T0_17_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_t0_17_t_symbol;
            case FONT_T0_17B_TF ->
                fontSel = Fonts.u8g2_font_t0_17b_tf;
            case FONT_T0_17B_TR ->
                fontSel = Fonts.u8g2_font_t0_17b_tr;
            case FONT_T0_17B_TN ->
                fontSel = Fonts.u8g2_font_t0_17b_tn;
            case FONT_T0_17B_TE ->
                fontSel = Fonts.u8g2_font_t0_17b_te;
            case FONT_T0_17B_MF ->
                fontSel = Fonts.u8g2_font_t0_17b_mf;
            case FONT_T0_17B_MR ->
                fontSel = Fonts.u8g2_font_t0_17b_mr;
            case FONT_T0_17B_MN ->
                fontSel = Fonts.u8g2_font_t0_17b_mn;
            case FONT_T0_17B_ME ->
                fontSel = Fonts.u8g2_font_t0_17b_me;
            case FONT_T0_18_TF ->
                fontSel = Fonts.u8g2_font_t0_18_tf;
            case FONT_T0_18_TR ->
                fontSel = Fonts.u8g2_font_t0_18_tr;
            case FONT_T0_18_TN ->
                fontSel = Fonts.u8g2_font_t0_18_tn;
            case FONT_T0_18_TE ->
                fontSel = Fonts.u8g2_font_t0_18_te;
            case FONT_T0_18_MF ->
                fontSel = Fonts.u8g2_font_t0_18_mf;
            case FONT_T0_18_MR ->
                fontSel = Fonts.u8g2_font_t0_18_mr;
            case FONT_T0_18_MN ->
                fontSel = Fonts.u8g2_font_t0_18_mn;
            case FONT_T0_18_ME ->
                fontSel = Fonts.u8g2_font_t0_18_me;
            case FONT_T0_18_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_t0_18_t_symbol;
            case FONT_T0_18B_TF ->
                fontSel = Fonts.u8g2_font_t0_18b_tf;
            case FONT_T0_18B_TR ->
                fontSel = Fonts.u8g2_font_t0_18b_tr;
            case FONT_T0_18B_TN ->
                fontSel = Fonts.u8g2_font_t0_18b_tn;
            case FONT_T0_18B_TE ->
                fontSel = Fonts.u8g2_font_t0_18b_te;
            case FONT_T0_18B_MF ->
                fontSel = Fonts.u8g2_font_t0_18b_mf;
            case FONT_T0_18B_MR ->
                fontSel = Fonts.u8g2_font_t0_18b_mr;
            case FONT_T0_18B_MN ->
                fontSel = Fonts.u8g2_font_t0_18b_mn;
            case FONT_T0_18B_ME ->
                fontSel = Fonts.u8g2_font_t0_18b_me;
            case FONT_T0_22_TF ->
                fontSel = Fonts.u8g2_font_t0_22_tf;
            case FONT_T0_22_TR ->
                fontSel = Fonts.u8g2_font_t0_22_tr;
            case FONT_T0_22_TN ->
                fontSel = Fonts.u8g2_font_t0_22_tn;
            case FONT_T0_22_TE ->
                fontSel = Fonts.u8g2_font_t0_22_te;
            case FONT_T0_22_MF ->
                fontSel = Fonts.u8g2_font_t0_22_mf;
            case FONT_T0_22_MR ->
                fontSel = Fonts.u8g2_font_t0_22_mr;
            case FONT_T0_22_MN ->
                fontSel = Fonts.u8g2_font_t0_22_mn;
            case FONT_T0_22_ME ->
                fontSel = Fonts.u8g2_font_t0_22_me;
            case FONT_T0_22_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_t0_22_t_symbol;
            case FONT_T0_22B_TF ->
                fontSel = Fonts.u8g2_font_t0_22b_tf;
            case FONT_T0_22B_TR ->
                fontSel = Fonts.u8g2_font_t0_22b_tr;
            case FONT_T0_22B_TN ->
                fontSel = Fonts.u8g2_font_t0_22b_tn;
            case FONT_T0_22B_TE ->
                fontSel = Fonts.u8g2_font_t0_22b_te;
            case FONT_T0_22B_MF ->
                fontSel = Fonts.u8g2_font_t0_22b_mf;
            case FONT_T0_22B_MR ->
                fontSel = Fonts.u8g2_font_t0_22b_mr;
            case FONT_T0_22B_MN ->
                fontSel = Fonts.u8g2_font_t0_22b_mn;
            case FONT_T0_22B_ME ->
                fontSel = Fonts.u8g2_font_t0_22b_me;
            case FONT_T0_30_TF ->
                fontSel = Fonts.u8g2_font_t0_30_tf;
            case FONT_T0_30_TR ->
                fontSel = Fonts.u8g2_font_t0_30_tr;
            case FONT_T0_30_TN ->
                fontSel = Fonts.u8g2_font_t0_30_tn;
            case FONT_T0_30_TE ->
                fontSel = Fonts.u8g2_font_t0_30_te;
            case FONT_T0_30_MF ->
                fontSel = Fonts.u8g2_font_t0_30_mf;
            case FONT_T0_30_MR ->
                fontSel = Fonts.u8g2_font_t0_30_mr;
            case FONT_T0_30_MN ->
                fontSel = Fonts.u8g2_font_t0_30_mn;
            case FONT_T0_30_ME ->
                fontSel = Fonts.u8g2_font_t0_30_me;
            case FONT_T0_30_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_t0_30_t_symbol;
            case FONT_T0_30B_TF ->
                fontSel = Fonts.u8g2_font_t0_30b_tf;
            case FONT_T0_30B_TR ->
                fontSel = Fonts.u8g2_font_t0_30b_tr;
            case FONT_T0_30B_TN ->
                fontSel = Fonts.u8g2_font_t0_30b_tn;
            case FONT_T0_30B_TE ->
                fontSel = Fonts.u8g2_font_t0_30b_te;
            case FONT_T0_30B_MF ->
                fontSel = Fonts.u8g2_font_t0_30b_mf;
            case FONT_T0_30B_MR ->
                fontSel = Fonts.u8g2_font_t0_30b_mr;
            case FONT_T0_30B_MN ->
                fontSel = Fonts.u8g2_font_t0_30b_mn;
            case FONT_T0_30B_ME ->
                fontSel = Fonts.u8g2_font_t0_30b_me;
            case FONT_T0_40_TF ->
                fontSel = Fonts.u8g2_font_t0_40_tf;
            case FONT_T0_40_TR ->
                fontSel = Fonts.u8g2_font_t0_40_tr;
            case FONT_T0_40_TN ->
                fontSel = Fonts.u8g2_font_t0_40_tn;
            case FONT_T0_40_TE ->
                fontSel = Fonts.u8g2_font_t0_40_te;
            case FONT_T0_40_MF ->
                fontSel = Fonts.u8g2_font_t0_40_mf;
            case FONT_T0_40_MR ->
                fontSel = Fonts.u8g2_font_t0_40_mr;
            case FONT_T0_40_MN ->
                fontSel = Fonts.u8g2_font_t0_40_mn;
            case FONT_T0_40_ME ->
                fontSel = Fonts.u8g2_font_t0_40_me;
            case FONT_T0_40_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_t0_40_t_symbol;
            case FONT_T0_40B_TF ->
                fontSel = Fonts.u8g2_font_t0_40b_tf;
            case FONT_T0_40B_TR ->
                fontSel = Fonts.u8g2_font_t0_40b_tr;
            case FONT_T0_40B_TN ->
                fontSel = Fonts.u8g2_font_t0_40b_tn;
            case FONT_T0_40B_TE ->
                fontSel = Fonts.u8g2_font_t0_40b_te;
            case FONT_T0_40B_MF ->
                fontSel = Fonts.u8g2_font_t0_40b_mf;
            case FONT_T0_40B_MR ->
                fontSel = Fonts.u8g2_font_t0_40b_mr;
            case FONT_T0_40B_MN ->
                fontSel = Fonts.u8g2_font_t0_40b_mn;
            case FONT_T0_40B_ME ->
                fontSel = Fonts.u8g2_font_t0_40b_me;
            case FONT_OPEN_ICONIC_ALL_1X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_all_1x_t;
            case FONT_OPEN_ICONIC_APP_1X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_app_1x_t;
            case FONT_OPEN_ICONIC_ARROW_1X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_arrow_1x_t;
            case FONT_OPEN_ICONIC_CHECK_1X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_check_1x_t;
            case FONT_OPEN_ICONIC_EMAIL_1X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_email_1x_t;
            case FONT_OPEN_ICONIC_EMBEDDED_1X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_embedded_1x_t;
            case FONT_OPEN_ICONIC_GUI_1X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_gui_1x_t;
            case FONT_OPEN_ICONIC_HUMAN_1X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_human_1x_t;
            case FONT_OPEN_ICONIC_MIME_1X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_mime_1x_t;
            case FONT_OPEN_ICONIC_OTHER_1X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_other_1x_t;
            case FONT_OPEN_ICONIC_PLAY_1X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_play_1x_t;
            case FONT_OPEN_ICONIC_TEXT_1X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_text_1x_t;
            case FONT_OPEN_ICONIC_THING_1X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_thing_1x_t;
            case FONT_OPEN_ICONIC_WEATHER_1X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_weather_1x_t;
            case FONT_OPEN_ICONIC_WWW_1X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_www_1x_t;
            case FONT_OPEN_ICONIC_ALL_2X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_all_2x_t;
            case FONT_OPEN_ICONIC_APP_2X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_app_2x_t;
            case FONT_OPEN_ICONIC_ARROW_2X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_arrow_2x_t;
            case FONT_OPEN_ICONIC_CHECK_2X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_check_2x_t;
            case FONT_OPEN_ICONIC_EMAIL_2X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_email_2x_t;
            case FONT_OPEN_ICONIC_EMBEDDED_2X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_embedded_2x_t;
            case FONT_OPEN_ICONIC_GUI_2X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_gui_2x_t;
            case FONT_OPEN_ICONIC_HUMAN_2X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_human_2x_t;
            case FONT_OPEN_ICONIC_MIME_2X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_mime_2x_t;
            case FONT_OPEN_ICONIC_OTHER_2X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_other_2x_t;
            case FONT_OPEN_ICONIC_PLAY_2X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_play_2x_t;
            case FONT_OPEN_ICONIC_TEXT_2X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_text_2x_t;
            case FONT_OPEN_ICONIC_THING_2X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_thing_2x_t;
            case FONT_OPEN_ICONIC_WEATHER_2X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_weather_2x_t;
            case FONT_OPEN_ICONIC_WWW_2X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_www_2x_t;
            case FONT_OPEN_ICONIC_ALL_4X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_all_4x_t;
            case FONT_OPEN_ICONIC_APP_4X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_app_4x_t;
            case FONT_OPEN_ICONIC_ARROW_4X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_arrow_4x_t;
            case FONT_OPEN_ICONIC_CHECK_4X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_check_4x_t;
            case FONT_OPEN_ICONIC_EMAIL_4X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_email_4x_t;
            case FONT_OPEN_ICONIC_EMBEDDED_4X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_embedded_4x_t;
            case FONT_OPEN_ICONIC_GUI_4X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_gui_4x_t;
            case FONT_OPEN_ICONIC_HUMAN_4X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_human_4x_t;
            case FONT_OPEN_ICONIC_MIME_4X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_mime_4x_t;
            case FONT_OPEN_ICONIC_OTHER_4X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_other_4x_t;
            case FONT_OPEN_ICONIC_PLAY_4X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_play_4x_t;
            case FONT_OPEN_ICONIC_TEXT_4X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_text_4x_t;
            case FONT_OPEN_ICONIC_THING_4X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_thing_4x_t;
            case FONT_OPEN_ICONIC_WEATHER_4X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_weather_4x_t;
            case FONT_OPEN_ICONIC_WWW_4X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_www_4x_t;
            case FONT_OPEN_ICONIC_ALL_6X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_all_6x_t;
            case FONT_OPEN_ICONIC_APP_6X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_app_6x_t;
            case FONT_OPEN_ICONIC_ARROW_6X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_arrow_6x_t;
            case FONT_OPEN_ICONIC_CHECK_6X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_check_6x_t;
            case FONT_OPEN_ICONIC_EMAIL_6X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_email_6x_t;
            case FONT_OPEN_ICONIC_EMBEDDED_6X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_embedded_6x_t;
            case FONT_OPEN_ICONIC_GUI_6X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_gui_6x_t;
            case FONT_OPEN_ICONIC_HUMAN_6X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_human_6x_t;
            case FONT_OPEN_ICONIC_MIME_6X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_mime_6x_t;
            case FONT_OPEN_ICONIC_OTHER_6X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_other_6x_t;
            case FONT_OPEN_ICONIC_PLAY_6X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_play_6x_t;
            case FONT_OPEN_ICONIC_TEXT_6X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_text_6x_t;
            case FONT_OPEN_ICONIC_THING_6X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_thing_6x_t;
            case FONT_OPEN_ICONIC_WEATHER_6X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_weather_6x_t;
            case FONT_OPEN_ICONIC_WWW_6X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_www_6x_t;
            case FONT_OPEN_ICONIC_ALL_8X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_all_8x_t;
            case FONT_OPEN_ICONIC_APP_8X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_app_8x_t;
            case FONT_OPEN_ICONIC_ARROW_8X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_arrow_8x_t;
            case FONT_OPEN_ICONIC_CHECK_8X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_check_8x_t;
            case FONT_OPEN_ICONIC_EMAIL_8X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_email_8x_t;
            case FONT_OPEN_ICONIC_EMBEDDED_8X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_embedded_8x_t;
            case FONT_OPEN_ICONIC_GUI_8X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_gui_8x_t;
            case FONT_OPEN_ICONIC_HUMAN_8X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_human_8x_t;
            case FONT_OPEN_ICONIC_MIME_8X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_mime_8x_t;
            case FONT_OPEN_ICONIC_OTHER_8X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_other_8x_t;
            case FONT_OPEN_ICONIC_PLAY_8X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_play_8x_t;
            case FONT_OPEN_ICONIC_TEXT_8X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_text_8x_t;
            case FONT_OPEN_ICONIC_THING_8X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_thing_8x_t;
            case FONT_OPEN_ICONIC_WEATHER_8X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_weather_8x_t;
            case FONT_OPEN_ICONIC_WWW_8X_T ->
                fontSel = Fonts.u8g2_font_open_iconic_www_8x_t;
            case FONT_STREAMLINE_ALL_T ->
                fontSel = Fonts.u8g2_font_streamline_all_t;
            case FONT_STREAMLINE_BUILDING_REAL_ESTATE_T ->
                fontSel = Fonts.u8g2_font_streamline_building_real_estate_t;
            case FONT_STREAMLINE_BUSINESS_T ->
                fontSel = Fonts.u8g2_font_streamline_business_t;
            case FONT_STREAMLINE_CODING_APPS_WEBSITES_T ->
                fontSel = Fonts.u8g2_font_streamline_coding_apps_websites_t;
            case FONT_STREAMLINE_COMPUTERS_DEVICES_ELECTRONICS_T ->
                fontSel = Fonts.u8g2_font_streamline_computers_devices_electronics_t;
            case FONT_STREAMLINE_CONTENT_FILES_T ->
                fontSel = Fonts.u8g2_font_streamline_content_files_t;
            case FONT_STREAMLINE_DESIGN_T ->
                fontSel = Fonts.u8g2_font_streamline_design_t;
            case FONT_STREAMLINE_ECOLOGY_T ->
                fontSel = Fonts.u8g2_font_streamline_ecology_t;
            case FONT_STREAMLINE_EMAIL_T ->
                fontSel = Fonts.u8g2_font_streamline_email_t;
            case FONT_STREAMLINE_ENTERTAINMENT_EVENTS_HOBBIES_T ->
                fontSel = Fonts.u8g2_font_streamline_entertainment_events_hobbies_t;
            case FONT_STREAMLINE_FOOD_DRINK_T ->
                fontSel = Fonts.u8g2_font_streamline_food_drink_t;
            case FONT_STREAMLINE_HAND_SIGNS_T ->
                fontSel = Fonts.u8g2_font_streamline_hand_signs_t;
            case FONT_STREAMLINE_HEALTH_BEAUTY_T ->
                fontSel = Fonts.u8g2_font_streamline_health_beauty_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_ACTION_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_action_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_ALERT_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_alert_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_AUDIO_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_audio_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_CALENDAR_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_calendar_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_CHART_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_chart_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_CIRCLE_TRIANGLE_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_circle_triangle_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_COG_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_cog_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_CURSOR_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_cursor_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_DIAL_PAD_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_dial_pad_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_EDIT_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_edit_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_EXPAND_SHRINK_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_expand_shrink_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_EYE_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_eye_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_FILE_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_file_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_HELP_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_help_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_HIERARCHY_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_hierarchy_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_HOME_MENU_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_home_menu_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_ID_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_id_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_KEY_LOCK_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_key_lock_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_LINK_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_link_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_LOADING_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_loading_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_LOGIN_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_login_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_OTHER_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_other_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_PAGINATE_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_paginate_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_SEARCH_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_search_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_SETTING_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_setting_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_SHARE_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_share_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_TEXT_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_text_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_WIFI_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_wifi_t;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_ZOOM_T ->
                fontSel = Fonts.u8g2_font_streamline_interface_essential_zoom_t;
            case FONT_STREAMLINE_INTERNET_NETWORK_T ->
                fontSel = Fonts.u8g2_font_streamline_internet_network_t;
            case FONT_STREAMLINE_LOGO_T ->
                fontSel = Fonts.u8g2_font_streamline_logo_t;
            case FONT_STREAMLINE_MAP_NAVIGATION_T ->
                fontSel = Fonts.u8g2_font_streamline_map_navigation_t;
            case FONT_STREAMLINE_MONEY_PAYMENTS_T ->
                fontSel = Fonts.u8g2_font_streamline_money_payments_t;
            case FONT_STREAMLINE_MUSIC_AUDIO_T ->
                fontSel = Fonts.u8g2_font_streamline_music_audio_t;
            case FONT_STREAMLINE_PET_ANIMALS_T ->
                fontSel = Fonts.u8g2_font_streamline_pet_animals_t;
            case FONT_STREAMLINE_PHONE_T ->
                fontSel = Fonts.u8g2_font_streamline_phone_t;
            case FONT_STREAMLINE_PHOTOGRAPHY_T ->
                fontSel = Fonts.u8g2_font_streamline_photography_t;
            case FONT_STREAMLINE_ROMANCE_T ->
                fontSel = Fonts.u8g2_font_streamline_romance_t;
            case FONT_STREAMLINE_SCHOOL_SCIENCE_T ->
                fontSel = Fonts.u8g2_font_streamline_school_science_t;
            case FONT_STREAMLINE_SHOPPING_SHIPPING_T ->
                fontSel = Fonts.u8g2_font_streamline_shopping_shipping_t;
            case FONT_STREAMLINE_SOCIAL_REWARDS_T ->
                fontSel = Fonts.u8g2_font_streamline_social_rewards_t;
            case FONT_STREAMLINE_TECHNOLOGY_T ->
                fontSel = Fonts.u8g2_font_streamline_technology_t;
            case FONT_STREAMLINE_TRANSPORTATION_T ->
                fontSel = Fonts.u8g2_font_streamline_transportation_t;
            case FONT_STREAMLINE_TRAVEL_WAYFINDING_T ->
                fontSel = Fonts.u8g2_font_streamline_travel_wayfinding_t;
            case FONT_STREAMLINE_USERS_T ->
                fontSel = Fonts.u8g2_font_streamline_users_t;
            case FONT_STREAMLINE_VIDEO_MOVIES_T ->
                fontSel = Fonts.u8g2_font_streamline_video_movies_t;
            case FONT_STREAMLINE_WEATHER_T ->
                fontSel = Fonts.u8g2_font_streamline_weather_t;
            case FONT_PROFONT10_TF ->
                fontSel = Fonts.u8g2_font_profont10_tf;
            case FONT_PROFONT10_TR ->
                fontSel = Fonts.u8g2_font_profont10_tr;
            case FONT_PROFONT10_TN ->
                fontSel = Fonts.u8g2_font_profont10_tn;
            case FONT_PROFONT10_MF ->
                fontSel = Fonts.u8g2_font_profont10_mf;
            case FONT_PROFONT10_MR ->
                fontSel = Fonts.u8g2_font_profont10_mr;
            case FONT_PROFONT10_MN ->
                fontSel = Fonts.u8g2_font_profont10_mn;
            case FONT_PROFONT11_TF ->
                fontSel = Fonts.u8g2_font_profont11_tf;
            case FONT_PROFONT11_TR ->
                fontSel = Fonts.u8g2_font_profont11_tr;
            case FONT_PROFONT11_TN ->
                fontSel = Fonts.u8g2_font_profont11_tn;
            case FONT_PROFONT11_MF ->
                fontSel = Fonts.u8g2_font_profont11_mf;
            case FONT_PROFONT11_MR ->
                fontSel = Fonts.u8g2_font_profont11_mr;
            case FONT_PROFONT11_MN ->
                fontSel = Fonts.u8g2_font_profont11_mn;
            case FONT_PROFONT12_TF ->
                fontSel = Fonts.u8g2_font_profont12_tf;
            case FONT_PROFONT12_TR ->
                fontSel = Fonts.u8g2_font_profont12_tr;
            case FONT_PROFONT12_TN ->
                fontSel = Fonts.u8g2_font_profont12_tn;
            case FONT_PROFONT12_MF ->
                fontSel = Fonts.u8g2_font_profont12_mf;
            case FONT_PROFONT12_MR ->
                fontSel = Fonts.u8g2_font_profont12_mr;
            case FONT_PROFONT12_MN ->
                fontSel = Fonts.u8g2_font_profont12_mn;
            case FONT_PROFONT15_TF ->
                fontSel = Fonts.u8g2_font_profont15_tf;
            case FONT_PROFONT15_TR ->
                fontSel = Fonts.u8g2_font_profont15_tr;
            case FONT_PROFONT15_TN ->
                fontSel = Fonts.u8g2_font_profont15_tn;
            case FONT_PROFONT15_MF ->
                fontSel = Fonts.u8g2_font_profont15_mf;
            case FONT_PROFONT15_MR ->
                fontSel = Fonts.u8g2_font_profont15_mr;
            case FONT_PROFONT15_MN ->
                fontSel = Fonts.u8g2_font_profont15_mn;
            case FONT_PROFONT17_TF ->
                fontSel = Fonts.u8g2_font_profont17_tf;
            case FONT_PROFONT17_TR ->
                fontSel = Fonts.u8g2_font_profont17_tr;
            case FONT_PROFONT17_TN ->
                fontSel = Fonts.u8g2_font_profont17_tn;
            case FONT_PROFONT17_MF ->
                fontSel = Fonts.u8g2_font_profont17_mf;
            case FONT_PROFONT17_MR ->
                fontSel = Fonts.u8g2_font_profont17_mr;
            case FONT_PROFONT17_MN ->
                fontSel = Fonts.u8g2_font_profont17_mn;
            case FONT_PROFONT22_TF ->
                fontSel = Fonts.u8g2_font_profont22_tf;
            case FONT_PROFONT22_TR ->
                fontSel = Fonts.u8g2_font_profont22_tr;
            case FONT_PROFONT22_TN ->
                fontSel = Fonts.u8g2_font_profont22_tn;
            case FONT_PROFONT22_MF ->
                fontSel = Fonts.u8g2_font_profont22_mf;
            case FONT_PROFONT22_MR ->
                fontSel = Fonts.u8g2_font_profont22_mr;
            case FONT_PROFONT22_MN ->
                fontSel = Fonts.u8g2_font_profont22_mn;
            case FONT_PROFONT29_TF ->
                fontSel = Fonts.u8g2_font_profont29_tf;
            case FONT_PROFONT29_TR ->
                fontSel = Fonts.u8g2_font_profont29_tr;
            case FONT_PROFONT29_TN ->
                fontSel = Fonts.u8g2_font_profont29_tn;
            case FONT_PROFONT29_MF ->
                fontSel = Fonts.u8g2_font_profont29_mf;
            case FONT_PROFONT29_MR ->
                fontSel = Fonts.u8g2_font_profont29_mr;
            case FONT_PROFONT29_MN ->
                fontSel = Fonts.u8g2_font_profont29_mn;
            case FONT_SAMIM_10_T_ALL ->
                fontSel = Fonts.u8g2_font_samim_10_t_all;
            case FONT_SAMIM_12_T_ALL ->
                fontSel = Fonts.u8g2_font_samim_12_t_all;
            case FONT_SAMIM_14_T_ALL ->
                fontSel = Fonts.u8g2_font_samim_14_t_all;
            case FONT_SAMIM_16_T_ALL ->
                fontSel = Fonts.u8g2_font_samim_16_t_all;
            case FONT_SAMIM_FD_10_T_ALL ->
                fontSel = Fonts.u8g2_font_samim_fd_10_t_all;
            case FONT_SAMIM_FD_12_T_ALL ->
                fontSel = Fonts.u8g2_font_samim_fd_12_t_all;
            case FONT_SAMIM_FD_14_T_ALL ->
                fontSel = Fonts.u8g2_font_samim_fd_14_t_all;
            case FONT_SAMIM_FD_16_T_ALL ->
                fontSel = Fonts.u8g2_font_samim_fd_16_t_all;
            case FONT_GANJ_NAMEH_SANS10_T_ALL ->
                fontSel = Fonts.u8g2_font_ganj_nameh_sans10_t_all;
            case FONT_GANJ_NAMEH_SANS12_T_ALL ->
                fontSel = Fonts.u8g2_font_ganj_nameh_sans12_t_all;
            case FONT_GANJ_NAMEH_SANS14_T_ALL ->
                fontSel = Fonts.u8g2_font_ganj_nameh_sans14_t_all;
            case FONT_GANJ_NAMEH_SANS16_T_ALL ->
                fontSel = Fonts.u8g2_font_ganj_nameh_sans16_t_all;
            case FONT_IRANIAN_SANS_8_T_ALL ->
                fontSel = Fonts.u8g2_font_iranian_sans_8_t_all;
            case FONT_IRANIAN_SANS_10_T_ALL ->
                fontSel = Fonts.u8g2_font_iranian_sans_10_t_all;
            case FONT_IRANIAN_SANS_12_T_ALL ->
                fontSel = Fonts.u8g2_font_iranian_sans_12_t_all;
            case FONT_IRANIAN_SANS_14_T_ALL ->
                fontSel = Fonts.u8g2_font_iranian_sans_14_t_all;
            case FONT_IRANIAN_SANS_16_T_ALL ->
                fontSel = Fonts.u8g2_font_iranian_sans_16_t_all;
            case FONT_MOZART_NBP_TF ->
                fontSel = Fonts.u8g2_font_mozart_nbp_tf;
            case FONT_MOZART_NBP_TR ->
                fontSel = Fonts.u8g2_font_mozart_nbp_tr;
            case FONT_MOZART_NBP_TN ->
                fontSel = Fonts.u8g2_font_mozart_nbp_tn;
            case FONT_MOZART_NBP_T_ALL ->
                fontSel = Fonts.u8g2_font_mozart_nbp_t_all;
            case FONT_MOZART_NBP_H_ALL ->
                fontSel = Fonts.u8g2_font_mozart_nbp_h_all;
            case FONT_GLASSTOWN_NBP_TF ->
                fontSel = Fonts.u8g2_font_glasstown_nbp_tf;
            case FONT_GLASSTOWN_NBP_TR ->
                fontSel = Fonts.u8g2_font_glasstown_nbp_tr;
            case FONT_GLASSTOWN_NBP_TN ->
                fontSel = Fonts.u8g2_font_glasstown_nbp_tn;
            case FONT_GLASSTOWN_NBP_T_ALL ->
                fontSel = Fonts.u8g2_font_glasstown_nbp_t_all;
            case FONT_SHYLOCK_NBP_TF ->
                fontSel = Fonts.u8g2_font_shylock_nbp_tf;
            case FONT_SHYLOCK_NBP_TR ->
                fontSel = Fonts.u8g2_font_shylock_nbp_tr;
            case FONT_SHYLOCK_NBP_TN ->
                fontSel = Fonts.u8g2_font_shylock_nbp_tn;
            case FONT_SHYLOCK_NBP_T_ALL ->
                fontSel = Fonts.u8g2_font_shylock_nbp_t_all;
            case FONT_ROENTGEN_NBP_TF ->
                fontSel = Fonts.u8g2_font_roentgen_nbp_tf;
            case FONT_ROENTGEN_NBP_TR ->
                fontSel = Fonts.u8g2_font_roentgen_nbp_tr;
            case FONT_ROENTGEN_NBP_TN ->
                fontSel = Fonts.u8g2_font_roentgen_nbp_tn;
            case FONT_ROENTGEN_NBP_T_ALL ->
                fontSel = Fonts.u8g2_font_roentgen_nbp_t_all;
            case FONT_ROENTGEN_NBP_H_ALL ->
                fontSel = Fonts.u8g2_font_roentgen_nbp_h_all;
            case FONT_CALIBRATION_GOTHIC_NBP_TF ->
                fontSel = Fonts.u8g2_font_calibration_gothic_nbp_tf;
            case FONT_CALIBRATION_GOTHIC_NBP_TR ->
                fontSel = Fonts.u8g2_font_calibration_gothic_nbp_tr;
            case FONT_CALIBRATION_GOTHIC_NBP_TN ->
                fontSel = Fonts.u8g2_font_calibration_gothic_nbp_tn;
            case FONT_CALIBRATION_GOTHIC_NBP_T_ALL ->
                fontSel = Fonts.u8g2_font_calibration_gothic_nbp_t_all;
            case FONT_SMART_PATROL_NBP_TF ->
                fontSel = Fonts.u8g2_font_smart_patrol_nbp_tf;
            case FONT_SMART_PATROL_NBP_TR ->
                fontSel = Fonts.u8g2_font_smart_patrol_nbp_tr;
            case FONT_SMART_PATROL_NBP_TN ->
                fontSel = Fonts.u8g2_font_smart_patrol_nbp_tn;
            case FONT_PROSPERO_BOLD_NBP_TF ->
                fontSel = Fonts.u8g2_font_prospero_bold_nbp_tf;
            case FONT_PROSPERO_BOLD_NBP_TR ->
                fontSel = Fonts.u8g2_font_prospero_bold_nbp_tr;
            case FONT_PROSPERO_BOLD_NBP_TN ->
                fontSel = Fonts.u8g2_font_prospero_bold_nbp_tn;
            case FONT_PROSPERO_NBP_TF ->
                fontSel = Fonts.u8g2_font_prospero_nbp_tf;
            case FONT_PROSPERO_NBP_TR ->
                fontSel = Fonts.u8g2_font_prospero_nbp_tr;
            case FONT_PROSPERO_NBP_TN ->
                fontSel = Fonts.u8g2_font_prospero_nbp_tn;
            case FONT_BALTHASAR_REGULAR_NBP_TF ->
                fontSel = Fonts.u8g2_font_balthasar_regular_nbp_tf;
            case FONT_BALTHASAR_REGULAR_NBP_TR ->
                fontSel = Fonts.u8g2_font_balthasar_regular_nbp_tr;
            case FONT_BALTHASAR_REGULAR_NBP_TN ->
                fontSel = Fonts.u8g2_font_balthasar_regular_nbp_tn;
            case FONT_BALTHASAR_TITLING_NBP_TF ->
                fontSel = Fonts.u8g2_font_balthasar_titling_nbp_tf;
            case FONT_BALTHASAR_TITLING_NBP_TR ->
                fontSel = Fonts.u8g2_font_balthasar_titling_nbp_tr;
            case FONT_BALTHASAR_TITLING_NBP_TN ->
                fontSel = Fonts.u8g2_font_balthasar_titling_nbp_tn;
            case FONT_SYNCHRONIZER_NBP_TF ->
                fontSel = Fonts.u8g2_font_synchronizer_nbp_tf;
            case FONT_SYNCHRONIZER_NBP_TR ->
                fontSel = Fonts.u8g2_font_synchronizer_nbp_tr;
            case FONT_SYNCHRONIZER_NBP_TN ->
                fontSel = Fonts.u8g2_font_synchronizer_nbp_tn;
            case FONT_MERCUTIO_BASIC_NBP_TF ->
                fontSel = Fonts.u8g2_font_mercutio_basic_nbp_tf;
            case FONT_MERCUTIO_BASIC_NBP_TR ->
                fontSel = Fonts.u8g2_font_mercutio_basic_nbp_tr;
            case FONT_MERCUTIO_BASIC_NBP_TN ->
                fontSel = Fonts.u8g2_font_mercutio_basic_nbp_tn;
            case FONT_MERCUTIO_BASIC_NBP_T_ALL ->
                fontSel = Fonts.u8g2_font_mercutio_basic_nbp_t_all;
            case FONT_MERCUTIO_SC_NBP_TF ->
                fontSel = Fonts.u8g2_font_mercutio_sc_nbp_tf;
            case FONT_MERCUTIO_SC_NBP_TR ->
                fontSel = Fonts.u8g2_font_mercutio_sc_nbp_tr;
            case FONT_MERCUTIO_SC_NBP_TN ->
                fontSel = Fonts.u8g2_font_mercutio_sc_nbp_tn;
            case FONT_MERCUTIO_SC_NBP_T_ALL ->
                fontSel = Fonts.u8g2_font_mercutio_sc_nbp_t_all;
            case FONT_MIRANDA_NBP_TF ->
                fontSel = Fonts.u8g2_font_miranda_nbp_tf;
            case FONT_MIRANDA_NBP_TR ->
                fontSel = Fonts.u8g2_font_miranda_nbp_tr;
            case FONT_MIRANDA_NBP_TN ->
                fontSel = Fonts.u8g2_font_miranda_nbp_tn;
            case FONT_NINE_BY_FIVE_NBP_TF ->
                fontSel = Fonts.u8g2_font_nine_by_five_nbp_tf;
            case FONT_NINE_BY_FIVE_NBP_TR ->
                fontSel = Fonts.u8g2_font_nine_by_five_nbp_tr;
            case FONT_NINE_BY_FIVE_NBP_TN ->
                fontSel = Fonts.u8g2_font_nine_by_five_nbp_tn;
            case FONT_NINE_BY_FIVE_NBP_T_ALL ->
                fontSel = Fonts.u8g2_font_nine_by_five_nbp_t_all;
            case FONT_ROSENCRANTZ_NBP_TF ->
                fontSel = Fonts.u8g2_font_rosencrantz_nbp_tf;
            case FONT_ROSENCRANTZ_NBP_TR ->
                fontSel = Fonts.u8g2_font_rosencrantz_nbp_tr;
            case FONT_ROSENCRANTZ_NBP_TN ->
                fontSel = Fonts.u8g2_font_rosencrantz_nbp_tn;
            case FONT_ROSENCRANTZ_NBP_T_ALL ->
                fontSel = Fonts.u8g2_font_rosencrantz_nbp_t_all;
            case FONT_GUILDENSTERN_NBP_TF ->
                fontSel = Fonts.u8g2_font_guildenstern_nbp_tf;
            case FONT_GUILDENSTERN_NBP_TR ->
                fontSel = Fonts.u8g2_font_guildenstern_nbp_tr;
            case FONT_GUILDENSTERN_NBP_TN ->
                fontSel = Fonts.u8g2_font_guildenstern_nbp_tn;
            case FONT_GUILDENSTERN_NBP_T_ALL ->
                fontSel = Fonts.u8g2_font_guildenstern_nbp_t_all;
            case FONT_ASTRAGAL_NBP_TF ->
                fontSel = Fonts.u8g2_font_astragal_nbp_tf;
            case FONT_ASTRAGAL_NBP_TR ->
                fontSel = Fonts.u8g2_font_astragal_nbp_tr;
            case FONT_ASTRAGAL_NBP_TN ->
                fontSel = Fonts.u8g2_font_astragal_nbp_tn;
            case FONT_HABSBURGCHANCERY_TF ->
                fontSel = Fonts.u8g2_font_habsburgchancery_tf;
            case FONT_HABSBURGCHANCERY_TR ->
                fontSel = Fonts.u8g2_font_habsburgchancery_tr;
            case FONT_HABSBURGCHANCERY_TN ->
                fontSel = Fonts.u8g2_font_habsburgchancery_tn;
            case FONT_HABSBURGCHANCERY_T_ALL ->
                fontSel = Fonts.u8g2_font_habsburgchancery_t_all;
            case FONT_MISSINGPLANET_TF ->
                fontSel = Fonts.u8g2_font_missingplanet_tf;
            case FONT_MISSINGPLANET_TR ->
                fontSel = Fonts.u8g2_font_missingplanet_tr;
            case FONT_MISSINGPLANET_TN ->
                fontSel = Fonts.u8g2_font_missingplanet_tn;
            case FONT_MISSINGPLANET_T_ALL ->
                fontSel = Fonts.u8g2_font_missingplanet_t_all;
            case FONT_ORDINARYBASIS_TF ->
                fontSel = Fonts.u8g2_font_ordinarybasis_tf;
            case FONT_ORDINARYBASIS_TR ->
                fontSel = Fonts.u8g2_font_ordinarybasis_tr;
            case FONT_ORDINARYBASIS_TN ->
                fontSel = Fonts.u8g2_font_ordinarybasis_tn;
            case FONT_ORDINARYBASIS_T_ALL ->
                fontSel = Fonts.u8g2_font_ordinarybasis_t_all;
            case FONT_PIXELMORDRED_TF ->
                fontSel = Fonts.u8g2_font_pixelmordred_tf;
            case FONT_PIXELMORDRED_TR ->
                fontSel = Fonts.u8g2_font_pixelmordred_tr;
            case FONT_PIXELMORDRED_TN ->
                fontSel = Fonts.u8g2_font_pixelmordred_tn;
            case FONT_PIXELMORDRED_T_ALL ->
                fontSel = Fonts.u8g2_font_pixelmordred_t_all;
            case FONT_SECRETARYHAND_TF ->
                fontSel = Fonts.u8g2_font_secretaryhand_tf;
            case FONT_SECRETARYHAND_TR ->
                fontSel = Fonts.u8g2_font_secretaryhand_tr;
            case FONT_SECRETARYHAND_TN ->
                fontSel = Fonts.u8g2_font_secretaryhand_tn;
            case FONT_SECRETARYHAND_T_ALL ->
                fontSel = Fonts.u8g2_font_secretaryhand_t_all;
            case FONT_GARBAGECAN_TF ->
                fontSel = Fonts.u8g2_font_garbagecan_tf;
            case FONT_GARBAGECAN_TR ->
                fontSel = Fonts.u8g2_font_garbagecan_tr;
            case FONT_BEANSTALK_MEL_TR ->
                fontSel = Fonts.u8g2_font_beanstalk_mel_tr;
            case FONT_BEANSTALK_MEL_TN ->
                fontSel = Fonts.u8g2_font_beanstalk_mel_tn;
            case FONT_CUBE_MEL_TR ->
                fontSel = Fonts.u8g2_font_cube_mel_tr;
            case FONT_CUBE_MEL_TN ->
                fontSel = Fonts.u8g2_font_cube_mel_tn;
            case FONT_MADEMOISELLE_MEL_TR ->
                fontSel = Fonts.u8g2_font_mademoiselle_mel_tr;
            case FONT_MADEMOISELLE_MEL_TN ->
                fontSel = Fonts.u8g2_font_mademoiselle_mel_tn;
            case FONT_PIECEOFCAKE_MEL_TR ->
                fontSel = Fonts.u8g2_font_pieceofcake_mel_tr;
            case FONT_PIECEOFCAKE_MEL_TN ->
                fontSel = Fonts.u8g2_font_pieceofcake_mel_tn;
            case FONT_PRESS_MEL_TR ->
                fontSel = Fonts.u8g2_font_press_mel_tr;
            case FONT_PRESS_MEL_TN ->
                fontSel = Fonts.u8g2_font_press_mel_tn;
            case FONT_REPRESS_MEL_TR ->
                fontSel = Fonts.u8g2_font_repress_mel_tr;
            case FONT_REPRESS_MEL_TN ->
                fontSel = Fonts.u8g2_font_repress_mel_tn;
            case FONT_STICKER_MEL_TR ->
                fontSel = Fonts.u8g2_font_sticker_mel_tr;
            case FONT_STICKER_MEL_TN ->
                fontSel = Fonts.u8g2_font_sticker_mel_tn;
            case FONT_CELIBATEMONK_TR ->
                fontSel = Fonts.u8g2_font_celibatemonk_tr;
            case FONT_DISRESPECTFULTEENAGER_TU ->
                fontSel = Fonts.u8g2_font_disrespectfulteenager_tu;
            case FONT_MICHAELMOUSE_TU ->
                fontSel = Fonts.u8g2_font_michaelmouse_tu;
            case FONT_SANDYFOREST_TR ->
                fontSel = Fonts.u8g2_font_sandyforest_tr;
            case FONT_SANDYFOREST_TN ->
                fontSel = Fonts.u8g2_font_sandyforest_tn;
            case FONT_SANDYFOREST_TU ->
                fontSel = Fonts.u8g2_font_sandyforest_tu;
            case FONT_CUPCAKEMETOYOURLEADER_TR ->
                fontSel = Fonts.u8g2_font_cupcakemetoyourleader_tr;
            case FONT_CUPCAKEMETOYOURLEADER_TN ->
                fontSel = Fonts.u8g2_font_cupcakemetoyourleader_tn;
            case FONT_CUPCAKEMETOYOURLEADER_TU ->
                fontSel = Fonts.u8g2_font_cupcakemetoyourleader_tu;
            case FONT_OLDWIZARD_TF ->
                fontSel = Fonts.u8g2_font_oldwizard_tf;
            case FONT_OLDWIZARD_TR ->
                fontSel = Fonts.u8g2_font_oldwizard_tr;
            case FONT_OLDWIZARD_TN ->
                fontSel = Fonts.u8g2_font_oldwizard_tn;
            case FONT_OLDWIZARD_TU ->
                fontSel = Fonts.u8g2_font_oldwizard_tu;
            case FONT_SQUIRREL_TR ->
                fontSel = Fonts.u8g2_font_squirrel_tr;
            case FONT_SQUIRREL_TN ->
                fontSel = Fonts.u8g2_font_squirrel_tn;
            case FONT_SQUIRREL_TU ->
                fontSel = Fonts.u8g2_font_squirrel_tu;
            case FONT_DIODESEMIMONO_TR ->
                fontSel = Fonts.u8g2_font_diodesemimono_tr;
            case FONT_QUESTGIVER_TR ->
                fontSel = Fonts.u8g2_font_questgiver_tr;
            case FONT_SERAPHIMB1_TR ->
                fontSel = Fonts.u8g2_font_seraphimb1_tr;
            case FONT_RESOLEDBOLD_TR ->
                fontSel = Fonts.u8g2_font_resoledbold_tr;
            case FONT_RESOLEDMEDIUM_TR ->
                fontSel = Fonts.u8g2_font_resoledmedium_tr;
            case FONT_JINXEDWIZARDS_TR ->
                fontSel = Fonts.u8g2_font_jinxedwizards_tr;
            case FONT_LASTPRIESTESS_TR ->
                fontSel = Fonts.u8g2_font_lastpriestess_tr;
            case FONT_LASTPRIESTESS_TU ->
                fontSel = Fonts.u8g2_font_lastpriestess_tu;
            case FONT_BITCASUAL_TF ->
                fontSel = Fonts.u8g2_font_bitcasual_tf;
            case FONT_BITCASUAL_TR ->
                fontSel = Fonts.u8g2_font_bitcasual_tr;
            case FONT_BITCASUAL_TN ->
                fontSel = Fonts.u8g2_font_bitcasual_tn;
            case FONT_BITCASUAL_TU ->
                fontSel = Fonts.u8g2_font_bitcasual_tu;
            case FONT_BITCASUAL_T_ALL ->
                fontSel = Fonts.u8g2_font_bitcasual_t_all;
            case FONT_KOLEEKO_TF ->
                fontSel = Fonts.u8g2_font_koleeko_tf;
            case FONT_KOLEEKO_TR ->
                fontSel = Fonts.u8g2_font_koleeko_tr;
            case FONT_KOLEEKO_TN ->
                fontSel = Fonts.u8g2_font_koleeko_tn;
            case FONT_KOLEEKO_TU ->
                fontSel = Fonts.u8g2_font_koleeko_tu;
            case FONT_TENFATGUYS_TF ->
                fontSel = Fonts.u8g2_font_tenfatguys_tf;
            case FONT_TENFATGUYS_TR ->
                fontSel = Fonts.u8g2_font_tenfatguys_tr;
            case FONT_TENFATGUYS_TN ->
                fontSel = Fonts.u8g2_font_tenfatguys_tn;
            case FONT_TENFATGUYS_TU ->
                fontSel = Fonts.u8g2_font_tenfatguys_tu;
            case FONT_TENFATGUYS_T_ALL ->
                fontSel = Fonts.u8g2_font_tenfatguys_t_all;
            case FONT_TENSTAMPS_MF ->
                fontSel = Fonts.u8g2_font_tenstamps_mf;
            case FONT_TENSTAMPS_MR ->
                fontSel = Fonts.u8g2_font_tenstamps_mr;
            case FONT_TENSTAMPS_MN ->
                fontSel = Fonts.u8g2_font_tenstamps_mn;
            case FONT_TENSTAMPS_MU ->
                fontSel = Fonts.u8g2_font_tenstamps_mu;
            case FONT_TENTHINGUYS_TF ->
                fontSel = Fonts.u8g2_font_tenthinguys_tf;
            case FONT_TENTHINGUYS_TR ->
                fontSel = Fonts.u8g2_font_tenthinguys_tr;
            case FONT_TENTHINGUYS_TN ->
                fontSel = Fonts.u8g2_font_tenthinguys_tn;
            case FONT_TENTHINGUYS_TU ->
                fontSel = Fonts.u8g2_font_tenthinguys_tu;
            case FONT_TENTHINGUYS_T_ALL ->
                fontSel = Fonts.u8g2_font_tenthinguys_t_all;
            case FONT_TENTHINNERGUYS_TF ->
                fontSel = Fonts.u8g2_font_tenthinnerguys_tf;
            case FONT_TENTHINNERGUYS_TR ->
                fontSel = Fonts.u8g2_font_tenthinnerguys_tr;
            case FONT_TENTHINNERGUYS_TN ->
                fontSel = Fonts.u8g2_font_tenthinnerguys_tn;
            case FONT_TENTHINNERGUYS_TU ->
                fontSel = Fonts.u8g2_font_tenthinnerguys_tu;
            case FONT_TENTHINNERGUYS_T_ALL ->
                fontSel = Fonts.u8g2_font_tenthinnerguys_t_all;
            case FONT_TWELVEDINGS_T_ALL ->
                fontSel = Fonts.u8g2_font_twelvedings_t_all;
            case FONT_FRIGIDAIRE_MR ->
                fontSel = Fonts.u8g2_font_frigidaire_mr;
            case FONT_LORD_MR ->
                fontSel = Fonts.u8g2_font_lord_mr;
            case FONT_ABEL_MR ->
                fontSel = Fonts.u8g2_font_abel_mr;
            case FONT_FEWTURE_TF ->
                fontSel = Fonts.u8g2_font_fewture_tf;
            case FONT_FEWTURE_TR ->
                fontSel = Fonts.u8g2_font_fewture_tr;
            case FONT_FEWTURE_TN ->
                fontSel = Fonts.u8g2_font_fewture_tn;
            case FONT_HALFTONE_TF ->
                fontSel = Fonts.u8g2_font_halftone_tf;
            case FONT_HALFTONE_TR ->
                fontSel = Fonts.u8g2_font_halftone_tr;
            case FONT_HALFTONE_TN ->
                fontSel = Fonts.u8g2_font_halftone_tn;
            case FONT_NERHOE_TF ->
                fontSel = Fonts.u8g2_font_nerhoe_tf;
            case FONT_NERHOE_TR ->
                fontSel = Fonts.u8g2_font_nerhoe_tr;
            case FONT_NERHOE_TN ->
                fontSel = Fonts.u8g2_font_nerhoe_tn;
            case FONT_OSKOOL_TF ->
                fontSel = Fonts.u8g2_font_oskool_tf;
            case FONT_OSKOOL_TR ->
                fontSel = Fonts.u8g2_font_oskool_tr;
            case FONT_OSKOOL_TN ->
                fontSel = Fonts.u8g2_font_oskool_tn;
            case FONT_TINYTIM_TF ->
                fontSel = Fonts.u8g2_font_tinytim_tf;
            case FONT_TINYTIM_TR ->
                fontSel = Fonts.u8g2_font_tinytim_tr;
            case FONT_TINYTIM_TN ->
                fontSel = Fonts.u8g2_font_tinytim_tn;
            case FONT_TOOSEORNAMENT_TF ->
                fontSel = Fonts.u8g2_font_tooseornament_tf;
            case FONT_TOOSEORNAMENT_TR ->
                fontSel = Fonts.u8g2_font_tooseornament_tr;
            case FONT_TOOSEORNAMENT_TN ->
                fontSel = Fonts.u8g2_font_tooseornament_tn;
            case FONT_BAUHAUS2015_TR ->
                fontSel = Fonts.u8g2_font_bauhaus2015_tr;
            case FONT_BAUHAUS2015_TN ->
                fontSel = Fonts.u8g2_font_bauhaus2015_tn;
            case FONT_FINDERSKEEPERS_TF ->
                fontSel = Fonts.u8g2_font_finderskeepers_tf;
            case FONT_FINDERSKEEPERS_TR ->
                fontSel = Fonts.u8g2_font_finderskeepers_tr;
            case FONT_FINDERSKEEPERS_TN ->
                fontSel = Fonts.u8g2_font_finderskeepers_tn;
            case FONT_SIRCLIVETHEBOLD_TR ->
                fontSel = Fonts.u8g2_font_sirclivethebold_tr;
            case FONT_SIRCLIVETHEBOLD_TN ->
                fontSel = Fonts.u8g2_font_sirclivethebold_tn;
            case FONT_SIRCLIVE_TR ->
                fontSel = Fonts.u8g2_font_sirclive_tr;
            case FONT_SIRCLIVE_TN ->
                fontSel = Fonts.u8g2_font_sirclive_tn;
            case FONT_ADVENTURER_TF ->
                fontSel = Fonts.u8g2_font_adventurer_tf;
            case FONT_ADVENTURER_TR ->
                fontSel = Fonts.u8g2_font_adventurer_tr;
            case FONT_ADVENTURER_T_ALL ->
                fontSel = Fonts.u8g2_font_adventurer_t_all;
            case FONT_BRACKETEDBABIES_TR ->
                fontSel = Fonts.u8g2_font_bracketedbabies_tr;
            case FONT_FRIKATIV_TF ->
                fontSel = Fonts.u8g2_font_frikativ_tf;
            case FONT_FRIKATIV_TR ->
                fontSel = Fonts.u8g2_font_frikativ_tr;
            case FONT_FRIKATIV_T_ALL ->
                fontSel = Fonts.u8g2_font_frikativ_t_all;
            case FONT_FANCYPIXELS_TF ->
                fontSel = Fonts.u8g2_font_fancypixels_tf;
            case FONT_FANCYPIXELS_TR ->
                fontSel = Fonts.u8g2_font_fancypixels_tr;
            case FONT_HEAVYBOTTOM_TR ->
                fontSel = Fonts.u8g2_font_heavybottom_tr;
            case FONT_ICONQUADPIX_M_ALL ->
                fontSel = Fonts.u8g2_font_iconquadpix_m_all;
            case FONT_TALLPIX_TR ->
                fontSel = Fonts.u8g2_font_tallpix_tr;
            case FONT_BOTMAKER_TE ->
                fontSel = Fonts.u8g2_font_botmaker_te;
            case FONT_EFRANEEXTRACONDENSED_TE ->
                fontSel = Fonts.u8g2_font_efraneextracondensed_te;
            case FONT_MINIMAL3X3_TU ->
                fontSel = Fonts.u8g2_font_minimal3x3_tu;
            case FONT_3X3BASIC_TR ->
                fontSel = Fonts.u8g2_font_3x3basic_tr;
            case FONT_TINY_GK_TR ->
                fontSel = Fonts.u8g2_font_tiny_gk_tr;
            case FONT_THREEPIX_TR ->
                fontSel = Fonts.u8g2_font_threepix_tr;
            case FONT_EVENTHREES_TR ->
                fontSel = Fonts.u8g2_font_eventhrees_tr;
            case FONT_FOURMAT_TF ->
                fontSel = Fonts.u8g2_font_fourmat_tf;
            case FONT_FOURMAT_TR ->
                fontSel = Fonts.u8g2_font_fourmat_tr;
            case FONT_FOURMAT_TE ->
                fontSel = Fonts.u8g2_font_fourmat_te;
            case FONT_TINY_SIMON_TR ->
                fontSel = Fonts.u8g2_font_tiny_simon_tr;
            case FONT_TINY_SIMON_MR ->
                fontSel = Fonts.u8g2_font_tiny_simon_mr;
            case FONT_SMOLFONT_TF ->
                fontSel = Fonts.u8g2_font_smolfont_tf;
            case FONT_SMOLFONT_TR ->
                fontSel = Fonts.u8g2_font_smolfont_tr;
            case FONT_SMOLFONT_TE ->
                fontSel = Fonts.u8g2_font_smolfont_te;
            case FONT_TINYUNICODE_TF ->
                fontSel = Fonts.u8g2_font_tinyunicode_tf;
            case FONT_TINYUNICODE_TR ->
                fontSel = Fonts.u8g2_font_tinyunicode_tr;
            case FONT_TINYUNICODE_TE ->
                fontSel = Fonts.u8g2_font_tinyunicode_te;
            case FONT_MICROPIXEL_TF ->
                fontSel = Fonts.u8g2_font_micropixel_tf;
            case FONT_MICROPIXEL_TR ->
                fontSel = Fonts.u8g2_font_micropixel_tr;
            case FONT_MICROPIXEL_TE ->
                fontSel = Fonts.u8g2_font_micropixel_te;
            case FONT_TINYPIXIE2_TR ->
                fontSel = Fonts.u8g2_font_tinypixie2_tr;
            case FONT_STANDARDIZED3X5_TR ->
                fontSel = Fonts.u8g2_font_standardized3x5_tr;
            case FONT_FIVEPX_TR ->
                fontSel = Fonts.u8g2_font_fivepx_tr;
            case FONT_3X5IM_TR ->
                fontSel = Fonts.u8g2_font_3x5im_tr;
            case FONT_3X5IM_TE ->
                fontSel = Fonts.u8g2_font_3x5im_te;
            case FONT_3X5IM_MR ->
                fontSel = Fonts.u8g2_font_3x5im_mr;
            case FONT_WEDGE_TR ->
                fontSel = Fonts.u8g2_font_wedge_tr;
            case FONT_KIBIBYTE_TR ->
                fontSel = Fonts.u8g2_font_kibibyte_tr;
            case FONT_KIBIBYTE_TE ->
                fontSel = Fonts.u8g2_font_kibibyte_te;
            case FONT_TINYFACE_TR ->
                fontSel = Fonts.u8g2_font_tinyface_tr;
            case FONT_TINYFACE_TE ->
                fontSel = Fonts.u8g2_font_tinyface_te;
            case FONT_SMALLSIMPLE_TR ->
                fontSel = Fonts.u8g2_font_smallsimple_tr;
            case FONT_SMALLSIMPLE_TE ->
                fontSel = Fonts.u8g2_font_smallsimple_te;
            case FONT_SIMPLE1_TF ->
                fontSel = Fonts.u8g2_font_simple1_tf;
            case FONT_SIMPLE1_TR ->
                fontSel = Fonts.u8g2_font_simple1_tr;
            case FONT_SIMPLE1_TE ->
                fontSel = Fonts.u8g2_font_simple1_te;
            case FONT_LIKEMINECRAFT_TE ->
                fontSel = Fonts.u8g2_font_likeminecraft_te;
            case FONT_MEDSANS_TR ->
                fontSel = Fonts.u8g2_font_medsans_tr;
            case FONT_HEISANS_TR ->
                fontSel = Fonts.u8g2_font_heisans_tr;
            case FONT_ORIGINALSANS_TR ->
                fontSel = Fonts.u8g2_font_originalsans_tr;
            case FONT_MINICUTE_TR ->
                fontSel = Fonts.u8g2_font_minicute_tr;
            case FONT_MINICUTE_TE ->
                fontSel = Fonts.u8g2_font_minicute_te;
            case FONT_SCRUM_TF ->
                fontSel = Fonts.u8g2_font_scrum_tf;
            case FONT_SCRUM_TR ->
                fontSel = Fonts.u8g2_font_scrum_tr;
            case FONT_SCRUM_TE ->
                fontSel = Fonts.u8g2_font_scrum_te;
            case FONT_STYLISHCHARM_TR ->
                fontSel = Fonts.u8g2_font_stylishcharm_tr;
            case FONT_STYLISHCHARM_TE ->
                fontSel = Fonts.u8g2_font_stylishcharm_te;
            case FONT_SISTERSERIF_TR ->
                fontSel = Fonts.u8g2_font_sisterserif_tr;
            case FONT_PRINCESS_TR ->
                fontSel = Fonts.u8g2_font_princess_tr;
            case FONT_PRINCESS_TE ->
                fontSel = Fonts.u8g2_font_princess_te;
            case FONT_DYSTOPIA_TR ->
                fontSel = Fonts.u8g2_font_dystopia_tr;
            case FONT_DYSTOPIA_TE ->
                fontSel = Fonts.u8g2_font_dystopia_te;
            case FONT_LASTAPPRENTICETHIN_TR ->
                fontSel = Fonts.u8g2_font_lastapprenticethin_tr;
            case FONT_LASTAPPRENTICETHIN_TE ->
                fontSel = Fonts.u8g2_font_lastapprenticethin_te;
            case FONT_LASTAPPRENTICEBOLD_TR ->
                fontSel = Fonts.u8g2_font_lastapprenticebold_tr;
            case FONT_LASTAPPRENTICEBOLD_TE ->
                fontSel = Fonts.u8g2_font_lastapprenticebold_te;
            case FONT_BPIXEL_TR ->
                fontSel = Fonts.u8g2_font_bpixel_tr;
            case FONT_BPIXEL_TE ->
                fontSel = Fonts.u8g2_font_bpixel_te;
            case FONT_BPIXELDOUBLE_TR ->
                fontSel = Fonts.u8g2_font_bpixeldouble_tr;
            case FONT_MILDRAS_TR ->
                fontSel = Fonts.u8g2_font_mildras_tr;
            case FONT_MILDRAS_TE ->
                fontSel = Fonts.u8g2_font_mildras_te;
            case FONT_MINUTECONSOLE_MR ->
                fontSel = Fonts.u8g2_font_minuteconsole_mr;
            case FONT_MINUTECONSOLE_TR ->
                fontSel = Fonts.u8g2_font_minuteconsole_tr;
            case FONT_BUSDISPLAY11X5_TR ->
                fontSel = Fonts.u8g2_font_busdisplay11x5_tr;
            case FONT_BUSDISPLAY11X5_TE ->
                fontSel = Fonts.u8g2_font_busdisplay11x5_te;
            case FONT_BUSDISPLAY8X5_TR ->
                fontSel = Fonts.u8g2_font_busdisplay8x5_tr;
            case FONT_STICKER100COMPLETE_TR ->
                fontSel = Fonts.u8g2_font_sticker100complete_tr;
            case FONT_STICKER100COMPLETE_TE ->
                fontSel = Fonts.u8g2_font_sticker100complete_te;
            case FONT_DOOMALPHA04_TR ->
                fontSel = Fonts.u8g2_font_doomalpha04_tr;
            case FONT_DOOMALPHA04_TE ->
                fontSel = Fonts.u8g2_font_doomalpha04_te;
            case FONT_GREENBLOODSERIF2_TR ->
                fontSel = Fonts.u8g2_font_greenbloodserif2_tr;
            case FONT_ECKPIXEL_TR ->
                fontSel = Fonts.u8g2_font_eckpixel_tr;
            case FONT_ELISPE_TR ->
                fontSel = Fonts.u8g2_font_elispe_tr;
            case FONT_NEUECRAFT_TR ->
                fontSel = Fonts.u8g2_font_neuecraft_tr;
            case FONT_NEUECRAFT_TE ->
                fontSel = Fonts.u8g2_font_neuecraft_te;
            case FONT_8BITCLASSIC_TF ->
                fontSel = Fonts.u8g2_font_8bitclassic_tf;
            case FONT_8BITCLASSIC_TR ->
                fontSel = Fonts.u8g2_font_8bitclassic_tr;
            case FONT_8BITCLASSIC_TE ->
                fontSel = Fonts.u8g2_font_8bitclassic_te;
            case FONT_LITTLEMISSLOUDONBOLD_TR ->
                fontSel = Fonts.u8g2_font_littlemissloudonbold_tr;
            case FONT_LITTLEMISSLOUDONBOLD_TE ->
                fontSel = Fonts.u8g2_font_littlemissloudonbold_te;
            case FONT_COMMODORE64_TR ->
                fontSel = Fonts.u8g2_font_commodore64_tr;
            case FONT_NEW3X9PIXELFONT_TF ->
                fontSel = Fonts.u8g2_font_new3x9pixelfont_tf;
            case FONT_NEW3X9PIXELFONT_TR ->
                fontSel = Fonts.u8g2_font_new3x9pixelfont_tr;
            case FONT_NEW3X9PIXELFONT_TE ->
                fontSel = Fonts.u8g2_font_new3x9pixelfont_te;
            case FONT_SONICMANIA_TR ->
                fontSel = Fonts.u8g2_font_sonicmania_tr;
            case FONT_SONICMANIA_TE ->
                fontSel = Fonts.u8g2_font_sonicmania_te;
            case FONT_BYTESIZE_TF ->
                fontSel = Fonts.u8g2_font_bytesize_tf;
            case FONT_BYTESIZE_TR ->
                fontSel = Fonts.u8g2_font_bytesize_tr;
            case FONT_BYTESIZE_TE ->
                fontSel = Fonts.u8g2_font_bytesize_te;
            case FONT_PIXZILLAV1_TF ->
                fontSel = Fonts.u8g2_font_pixzillav1_tf;
            case FONT_PIXZILLAV1_TR ->
                fontSel = Fonts.u8g2_font_pixzillav1_tr;
            case FONT_PIXZILLAV1_TE ->
                fontSel = Fonts.u8g2_font_pixzillav1_te;
            case FONT_CIIRCLE13_TR ->
                fontSel = Fonts.u8g2_font_ciircle13_tr;
            case FONT_PXCLASSIC_TF ->
                fontSel = Fonts.u8g2_font_pxclassic_tf;
            case FONT_PXCLASSIC_TR ->
                fontSel = Fonts.u8g2_font_pxclassic_tr;
            case FONT_PXCLASSIC_TE ->
                fontSel = Fonts.u8g2_font_pxclassic_te;
            case FONT_MOOSENOOKS_TR ->
                fontSel = Fonts.u8g2_font_moosenooks_tr;
            case FONT_TALLPIXELEXTENDED_TF ->
                fontSel = Fonts.u8g2_font_tallpixelextended_tf;
            case FONT_TALLPIXELEXTENDED_TR ->
                fontSel = Fonts.u8g2_font_tallpixelextended_tr;
            case FONT_TALLPIXELEXTENDED_TE ->
                fontSel = Fonts.u8g2_font_tallpixelextended_te;
            case FONT_BBSESQUE_TF ->
                fontSel = Fonts.u8g2_font_BBSesque_tf;
            case FONT_BBSESQUE_TR ->
                fontSel = Fonts.u8g2_font_BBSesque_tr;
            case FONT_BBSESQUE_TE ->
                fontSel = Fonts.u8g2_font_BBSesque_te;
            case FONT_BORN2BSPORTYSLAB_TF ->
                fontSel = Fonts.u8g2_font_Born2bSportySlab_tf;
            case FONT_BORN2BSPORTYSLAB_TR ->
                fontSel = Fonts.u8g2_font_Born2bSportySlab_tr;
            case FONT_BORN2BSPORTYSLAB_TE ->
                fontSel = Fonts.u8g2_font_Born2bSportySlab_te;
            case FONT_BORN2BSPORTYSLAB_T_ALL ->
                fontSel = Fonts.u8g2_font_Born2bSportySlab_t_all;
            case FONT_BORN2BSPORTYV2_TF ->
                fontSel = Fonts.u8g2_font_Born2bSportyV2_tf;
            case FONT_BORN2BSPORTYV2_TR ->
                fontSel = Fonts.u8g2_font_Born2bSportyV2_tr;
            case FONT_BORN2BSPORTYV2_TE ->
                fontSel = Fonts.u8g2_font_Born2bSportyV2_te;
            case FONT_CURSIVEPIXEL_TR ->
                fontSel = Fonts.u8g2_font_CursivePixel_tr;
            case FONT_ENGRISH_TF ->
                fontSel = Fonts.u8g2_font_Engrish_tf;
            case FONT_ENGRISH_TR ->
                fontSel = Fonts.u8g2_font_Engrish_tr;
            case FONT_IMPACTBITS_TR ->
                fontSel = Fonts.u8g2_font_ImpactBits_tr;
            case FONT_IPAANDRUSLCD_TF ->
                fontSel = Fonts.u8g2_font_IPAandRUSLCD_tf;
            case FONT_IPAANDRUSLCD_TR ->
                fontSel = Fonts.u8g2_font_IPAandRUSLCD_tr;
            case FONT_IPAANDRUSLCD_TE ->
                fontSel = Fonts.u8g2_font_IPAandRUSLCD_te;
            case FONT_PIXELTHEATRE_TR ->
                fontSel = Fonts.u8g2_font_PixelTheatre_tr;
            case FONT_PIXELTHEATRE_TE ->
                fontSel = Fonts.u8g2_font_PixelTheatre_te;
            case FONT_HELVETIPIXEL_TR ->
                fontSel = Fonts.u8g2_font_HelvetiPixel_tr;
            case FONT_TIMESNEWPIXEL_TR ->
                fontSel = Fonts.u8g2_font_TimesNewPixel_tr;
            case FONT_BITTYPEWRITER_TR ->
                fontSel = Fonts.u8g2_font_BitTypeWriter_tr;
            case FONT_BITTYPEWRITER_TE ->
                fontSel = Fonts.u8g2_font_BitTypeWriter_te;
            case FONT_GEORGIA7PX_TF ->
                fontSel = Fonts.u8g2_font_Georgia7px_tf;
            case FONT_GEORGIA7PX_TR ->
                fontSel = Fonts.u8g2_font_Georgia7px_tr;
            case FONT_GEORGIA7PX_TE ->
                fontSel = Fonts.u8g2_font_Georgia7px_te;
            case FONT_WIZZARD_TR ->
                fontSel = Fonts.u8g2_font_Wizzard_tr;
            case FONT_HELVETIPIXELOUTLINE_TR ->
                fontSel = Fonts.u8g2_font_HelvetiPixelOutline_tr;
            case FONT_HELVETIPIXELOUTLINE_TE ->
                fontSel = Fonts.u8g2_font_HelvetiPixelOutline_te;
            case FONT_UNTITLED16PIXELSANSSERIFBITMAP_TR ->
                fontSel = Fonts.u8g2_font_Untitled16PixelSansSerifBitmap_tr;
            case FONT_UNNAMEDDOSFONTIV_TR ->
                fontSel = Fonts.u8g2_font_UnnamedDOSFontIV_tr;
            case FONT_TERMINAL_TR ->
                fontSel = Fonts.u8g2_font_Terminal_tr;
            case FONT_TERMINAL_TE ->
                fontSel = Fonts.u8g2_font_Terminal_te;
            case FONT_NOKIALARGEBOLD_TF ->
                fontSel = Fonts.u8g2_font_NokiaLargeBold_tf;
            case FONT_NOKIALARGEBOLD_TR ->
                fontSel = Fonts.u8g2_font_NokiaLargeBold_tr;
            case FONT_NOKIALARGEBOLD_TE ->
                fontSel = Fonts.u8g2_font_NokiaLargeBold_te;
            case FONT_NOKIASMALLBOLD_TF ->
                fontSel = Fonts.u8g2_font_NokiaSmallBold_tf;
            case FONT_NOKIASMALLBOLD_TR ->
                fontSel = Fonts.u8g2_font_NokiaSmallBold_tr;
            case FONT_NOKIASMALLBOLD_TE ->
                fontSel = Fonts.u8g2_font_NokiaSmallBold_te;
            case FONT_NOKIASMALLPLAIN_TF ->
                fontSel = Fonts.u8g2_font_NokiaSmallPlain_tf;
            case FONT_NOKIASMALLPLAIN_TR ->
                fontSel = Fonts.u8g2_font_NokiaSmallPlain_tr;
            case FONT_NOKIASMALLPLAIN_TE ->
                fontSel = Fonts.u8g2_font_NokiaSmallPlain_te;
            case FONT_12X6LED_TF ->
                fontSel = Fonts.u8g2_font_12x6LED_tf;
            case FONT_12X6LED_TR ->
                fontSel = Fonts.u8g2_font_12x6LED_tr;
            case FONT_12X6LED_MN ->
                fontSel = Fonts.u8g2_font_12x6LED_mn;
            case FONT_9X6LED_TF ->
                fontSel = Fonts.u8g2_font_9x6LED_tf;
            case FONT_9X6LED_TR ->
                fontSel = Fonts.u8g2_font_9x6LED_tr;
            case FONT_9X6LED_MN ->
                fontSel = Fonts.u8g2_font_9x6LED_mn;
            case FONT_CALBLK36_TR ->
                fontSel = Fonts.u8g2_font_calblk36_tr;
            case FONT_CALLITE24_TR ->
                fontSel = Fonts.u8g2_font_callite24_tr;
            case FONT_SPLEEN5X8_MF ->
                fontSel = Fonts.u8g2_font_spleen5x8_mf;
            case FONT_SPLEEN5X8_MR ->
                fontSel = Fonts.u8g2_font_spleen5x8_mr;
            case FONT_SPLEEN5X8_MN ->
                fontSel = Fonts.u8g2_font_spleen5x8_mn;
            case FONT_SPLEEN5X8_MU ->
                fontSel = Fonts.u8g2_font_spleen5x8_mu;
            case FONT_SPLEEN5X8_ME ->
                fontSel = Fonts.u8g2_font_spleen5x8_me;
            case FONT_SPLEEN6X12_MF ->
                fontSel = Fonts.u8g2_font_spleen6x12_mf;
            case FONT_SPLEEN6X12_MR ->
                fontSel = Fonts.u8g2_font_spleen6x12_mr;
            case FONT_SPLEEN6X12_MN ->
                fontSel = Fonts.u8g2_font_spleen6x12_mn;
            case FONT_SPLEEN6X12_MU ->
                fontSel = Fonts.u8g2_font_spleen6x12_mu;
            case FONT_SPLEEN6X12_ME ->
                fontSel = Fonts.u8g2_font_spleen6x12_me;
            case FONT_SPLEEN8X16_MF ->
                fontSel = Fonts.u8g2_font_spleen8x16_mf;
            case FONT_SPLEEN8X16_MR ->
                fontSel = Fonts.u8g2_font_spleen8x16_mr;
            case FONT_SPLEEN8X16_MN ->
                fontSel = Fonts.u8g2_font_spleen8x16_mn;
            case FONT_SPLEEN8X16_MU ->
                fontSel = Fonts.u8g2_font_spleen8x16_mu;
            case FONT_SPLEEN8X16_ME ->
                fontSel = Fonts.u8g2_font_spleen8x16_me;
            case FONT_SPLEEN12X24_MF ->
                fontSel = Fonts.u8g2_font_spleen12x24_mf;
            case FONT_SPLEEN12X24_MR ->
                fontSel = Fonts.u8g2_font_spleen12x24_mr;
            case FONT_SPLEEN12X24_MN ->
                fontSel = Fonts.u8g2_font_spleen12x24_mn;
            case FONT_SPLEEN12X24_MU ->
                fontSel = Fonts.u8g2_font_spleen12x24_mu;
            case FONT_SPLEEN12X24_ME ->
                fontSel = Fonts.u8g2_font_spleen12x24_me;
            case FONT_SPLEEN16X32_MF ->
                fontSel = Fonts.u8g2_font_spleen16x32_mf;
            case FONT_SPLEEN16X32_MR ->
                fontSel = Fonts.u8g2_font_spleen16x32_mr;
            case FONT_SPLEEN16X32_MN ->
                fontSel = Fonts.u8g2_font_spleen16x32_mn;
            case FONT_SPLEEN16X32_MU ->
                fontSel = Fonts.u8g2_font_spleen16x32_mu;
            case FONT_SPLEEN16X32_ME ->
                fontSel = Fonts.u8g2_font_spleen16x32_me;
            case FONT_SPLEEN32X64_MF ->
                fontSel = Fonts.u8g2_font_spleen32x64_mf;
            case FONT_SPLEEN32X64_MR ->
                fontSel = Fonts.u8g2_font_spleen32x64_mr;
            case FONT_SPLEEN32X64_MN ->
                fontSel = Fonts.u8g2_font_spleen32x64_mn;
            case FONT_SPLEEN32X64_MU ->
                fontSel = Fonts.u8g2_font_spleen32x64_mu;
            case FONT_SPLEEN32X64_ME ->
                fontSel = Fonts.u8g2_font_spleen32x64_me;
            case FONT_NOKIAFC22_TF ->
                fontSel = Fonts.u8g2_font_nokiafc22_tf;
            case FONT_NOKIAFC22_TR ->
                fontSel = Fonts.u8g2_font_nokiafc22_tr;
            case FONT_NOKIAFC22_TN ->
                fontSel = Fonts.u8g2_font_nokiafc22_tn;
            case FONT_NOKIAFC22_TU ->
                fontSel = Fonts.u8g2_font_nokiafc22_tu;
            case FONT_VCR_OSD_TF ->
                fontSel = Fonts.u8g2_font_VCR_OSD_tf;
            case FONT_VCR_OSD_TR ->
                fontSel = Fonts.u8g2_font_VCR_OSD_tr;
            case FONT_VCR_OSD_TN ->
                fontSel = Fonts.u8g2_font_VCR_OSD_tn;
            case FONT_VCR_OSD_TU ->
                fontSel = Fonts.u8g2_font_VCR_OSD_tu;
            case FONT_VCR_OSD_MF ->
                fontSel = Fonts.u8g2_font_VCR_OSD_mf;
            case FONT_VCR_OSD_MR ->
                fontSel = Fonts.u8g2_font_VCR_OSD_mr;
            case FONT_VCR_OSD_MN ->
                fontSel = Fonts.u8g2_font_VCR_OSD_mn;
            case FONT_VCR_OSD_MU ->
                fontSel = Fonts.u8g2_font_VCR_OSD_mu;
            case FONT_PIXELLARI_TF ->
                fontSel = Fonts.u8g2_font_Pixellari_tf;
            case FONT_PIXELLARI_TR ->
                fontSel = Fonts.u8g2_font_Pixellari_tr;
            case FONT_PIXELLARI_TN ->
                fontSel = Fonts.u8g2_font_Pixellari_tn;
            case FONT_PIXELLARI_TU ->
                fontSel = Fonts.u8g2_font_Pixellari_tu;
            case FONT_PIXELLARI_TE ->
                fontSel = Fonts.u8g2_font_Pixellari_te;
            case FONT_PIXELPOIIZ_TR ->
                fontSel = Fonts.u8g2_font_pixelpoiiz_tr;
            case FONT_DIGITALDISCOTHIN_TF ->
                fontSel = Fonts.u8g2_font_DigitalDiscoThin_tf;
            case FONT_DIGITALDISCOTHIN_TR ->
                fontSel = Fonts.u8g2_font_DigitalDiscoThin_tr;
            case FONT_DIGITALDISCOTHIN_TN ->
                fontSel = Fonts.u8g2_font_DigitalDiscoThin_tn;
            case FONT_DIGITALDISCOTHIN_TU ->
                fontSel = Fonts.u8g2_font_DigitalDiscoThin_tu;
            case FONT_DIGITALDISCOTHIN_TE ->
                fontSel = Fonts.u8g2_font_DigitalDiscoThin_te;
            case FONT_DIGITALDISCO_TF ->
                fontSel = Fonts.u8g2_font_DigitalDisco_tf;
            case FONT_DIGITALDISCO_TR ->
                fontSel = Fonts.u8g2_font_DigitalDisco_tr;
            case FONT_DIGITALDISCO_TN ->
                fontSel = Fonts.u8g2_font_DigitalDisco_tn;
            case FONT_DIGITALDISCO_TU ->
                fontSel = Fonts.u8g2_font_DigitalDisco_tu;
            case FONT_DIGITALDISCO_TE ->
                fontSel = Fonts.u8g2_font_DigitalDisco_te;
            case FONT_PEARFONT_TR ->
                fontSel = Fonts.u8g2_font_pearfont_tr;
            case FONT_ETL14THAI_T ->
                fontSel = Fonts.u8g2_font_etl14thai_t;
            case FONT_ETL16THAI_T ->
                fontSel = Fonts.u8g2_font_etl16thai_t;
            case FONT_ETL24THAI_T ->
                fontSel = Fonts.u8g2_font_etl24thai_t;
            case FONT_CROX1CB_TF ->
                fontSel = Fonts.u8g2_font_crox1cb_tf;
            case FONT_CROX1CB_TR ->
                fontSel = Fonts.u8g2_font_crox1cb_tr;
            case FONT_CROX1CB_TN ->
                fontSel = Fonts.u8g2_font_crox1cb_tn;
            case FONT_CROX1CB_MF ->
                fontSel = Fonts.u8g2_font_crox1cb_mf;
            case FONT_CROX1CB_MR ->
                fontSel = Fonts.u8g2_font_crox1cb_mr;
            case FONT_CROX1CB_MN ->
                fontSel = Fonts.u8g2_font_crox1cb_mn;
            case FONT_CROX1C_TF ->
                fontSel = Fonts.u8g2_font_crox1c_tf;
            case FONT_CROX1C_TR ->
                fontSel = Fonts.u8g2_font_crox1c_tr;
            case FONT_CROX1C_TN ->
                fontSel = Fonts.u8g2_font_crox1c_tn;
            case FONT_CROX1C_MF ->
                fontSel = Fonts.u8g2_font_crox1c_mf;
            case FONT_CROX1C_MR ->
                fontSel = Fonts.u8g2_font_crox1c_mr;
            case FONT_CROX1C_MN ->
                fontSel = Fonts.u8g2_font_crox1c_mn;
            case FONT_CROX1HB_TF ->
                fontSel = Fonts.u8g2_font_crox1hb_tf;
            case FONT_CROX1HB_TR ->
                fontSel = Fonts.u8g2_font_crox1hb_tr;
            case FONT_CROX1HB_TN ->
                fontSel = Fonts.u8g2_font_crox1hb_tn;
            case FONT_CROX1H_TF ->
                fontSel = Fonts.u8g2_font_crox1h_tf;
            case FONT_CROX1H_TR ->
                fontSel = Fonts.u8g2_font_crox1h_tr;
            case FONT_CROX1H_TN ->
                fontSel = Fonts.u8g2_font_crox1h_tn;
            case FONT_CROX1TB_TF ->
                fontSel = Fonts.u8g2_font_crox1tb_tf;
            case FONT_CROX1TB_TR ->
                fontSel = Fonts.u8g2_font_crox1tb_tr;
            case FONT_CROX1TB_TN ->
                fontSel = Fonts.u8g2_font_crox1tb_tn;
            case FONT_CROX1T_TF ->
                fontSel = Fonts.u8g2_font_crox1t_tf;
            case FONT_CROX1T_TR ->
                fontSel = Fonts.u8g2_font_crox1t_tr;
            case FONT_CROX1T_TN ->
                fontSel = Fonts.u8g2_font_crox1t_tn;
            case FONT_CROX2CB_TF ->
                fontSel = Fonts.u8g2_font_crox2cb_tf;
            case FONT_CROX2CB_TR ->
                fontSel = Fonts.u8g2_font_crox2cb_tr;
            case FONT_CROX2CB_TN ->
                fontSel = Fonts.u8g2_font_crox2cb_tn;
            case FONT_CROX2CB_MF ->
                fontSel = Fonts.u8g2_font_crox2cb_mf;
            case FONT_CROX2CB_MR ->
                fontSel = Fonts.u8g2_font_crox2cb_mr;
            case FONT_CROX2CB_MN ->
                fontSel = Fonts.u8g2_font_crox2cb_mn;
            case FONT_CROX2C_TF ->
                fontSel = Fonts.u8g2_font_crox2c_tf;
            case FONT_CROX2C_TR ->
                fontSel = Fonts.u8g2_font_crox2c_tr;
            case FONT_CROX2C_TN ->
                fontSel = Fonts.u8g2_font_crox2c_tn;
            case FONT_CROX2C_MF ->
                fontSel = Fonts.u8g2_font_crox2c_mf;
            case FONT_CROX2C_MR ->
                fontSel = Fonts.u8g2_font_crox2c_mr;
            case FONT_CROX2C_MN ->
                fontSel = Fonts.u8g2_font_crox2c_mn;
            case FONT_CROX2HB_TF ->
                fontSel = Fonts.u8g2_font_crox2hb_tf;
            case FONT_CROX2HB_TR ->
                fontSel = Fonts.u8g2_font_crox2hb_tr;
            case FONT_CROX2HB_TN ->
                fontSel = Fonts.u8g2_font_crox2hb_tn;
            case FONT_CROX2H_TF ->
                fontSel = Fonts.u8g2_font_crox2h_tf;
            case FONT_CROX2H_TR ->
                fontSel = Fonts.u8g2_font_crox2h_tr;
            case FONT_CROX2H_TN ->
                fontSel = Fonts.u8g2_font_crox2h_tn;
            case FONT_CROX2TB_TF ->
                fontSel = Fonts.u8g2_font_crox2tb_tf;
            case FONT_CROX2TB_TR ->
                fontSel = Fonts.u8g2_font_crox2tb_tr;
            case FONT_CROX2TB_TN ->
                fontSel = Fonts.u8g2_font_crox2tb_tn;
            case FONT_CROX2T_TF ->
                fontSel = Fonts.u8g2_font_crox2t_tf;
            case FONT_CROX2T_TR ->
                fontSel = Fonts.u8g2_font_crox2t_tr;
            case FONT_CROX2T_TN ->
                fontSel = Fonts.u8g2_font_crox2t_tn;
            case FONT_CROX3CB_TF ->
                fontSel = Fonts.u8g2_font_crox3cb_tf;
            case FONT_CROX3CB_TR ->
                fontSel = Fonts.u8g2_font_crox3cb_tr;
            case FONT_CROX3CB_TN ->
                fontSel = Fonts.u8g2_font_crox3cb_tn;
            case FONT_CROX3CB_MF ->
                fontSel = Fonts.u8g2_font_crox3cb_mf;
            case FONT_CROX3CB_MR ->
                fontSel = Fonts.u8g2_font_crox3cb_mr;
            case FONT_CROX3CB_MN ->
                fontSel = Fonts.u8g2_font_crox3cb_mn;
            case FONT_CROX3C_TF ->
                fontSel = Fonts.u8g2_font_crox3c_tf;
            case FONT_CROX3C_TR ->
                fontSel = Fonts.u8g2_font_crox3c_tr;
            case FONT_CROX3C_TN ->
                fontSel = Fonts.u8g2_font_crox3c_tn;
            case FONT_CROX3C_MF ->
                fontSel = Fonts.u8g2_font_crox3c_mf;
            case FONT_CROX3C_MR ->
                fontSel = Fonts.u8g2_font_crox3c_mr;
            case FONT_CROX3C_MN ->
                fontSel = Fonts.u8g2_font_crox3c_mn;
            case FONT_CROX3HB_TF ->
                fontSel = Fonts.u8g2_font_crox3hb_tf;
            case FONT_CROX3HB_TR ->
                fontSel = Fonts.u8g2_font_crox3hb_tr;
            case FONT_CROX3HB_TN ->
                fontSel = Fonts.u8g2_font_crox3hb_tn;
            case FONT_CROX3H_TF ->
                fontSel = Fonts.u8g2_font_crox3h_tf;
            case FONT_CROX3H_TR ->
                fontSel = Fonts.u8g2_font_crox3h_tr;
            case FONT_CROX3H_TN ->
                fontSel = Fonts.u8g2_font_crox3h_tn;
            case FONT_CROX3TB_TF ->
                fontSel = Fonts.u8g2_font_crox3tb_tf;
            case FONT_CROX3TB_TR ->
                fontSel = Fonts.u8g2_font_crox3tb_tr;
            case FONT_CROX3TB_TN ->
                fontSel = Fonts.u8g2_font_crox3tb_tn;
            case FONT_CROX3T_TF ->
                fontSel = Fonts.u8g2_font_crox3t_tf;
            case FONT_CROX3T_TR ->
                fontSel = Fonts.u8g2_font_crox3t_tr;
            case FONT_CROX3T_TN ->
                fontSel = Fonts.u8g2_font_crox3t_tn;
            case FONT_CROX4HB_TF ->
                fontSel = Fonts.u8g2_font_crox4hb_tf;
            case FONT_CROX4HB_TR ->
                fontSel = Fonts.u8g2_font_crox4hb_tr;
            case FONT_CROX4HB_TN ->
                fontSel = Fonts.u8g2_font_crox4hb_tn;
            case FONT_CROX4H_TF ->
                fontSel = Fonts.u8g2_font_crox4h_tf;
            case FONT_CROX4H_TR ->
                fontSel = Fonts.u8g2_font_crox4h_tr;
            case FONT_CROX4H_TN ->
                fontSel = Fonts.u8g2_font_crox4h_tn;
            case FONT_CROX4TB_TF ->
                fontSel = Fonts.u8g2_font_crox4tb_tf;
            case FONT_CROX4TB_TR ->
                fontSel = Fonts.u8g2_font_crox4tb_tr;
            case FONT_CROX4TB_TN ->
                fontSel = Fonts.u8g2_font_crox4tb_tn;
            case FONT_CROX4T_TF ->
                fontSel = Fonts.u8g2_font_crox4t_tf;
            case FONT_CROX4T_TR ->
                fontSel = Fonts.u8g2_font_crox4t_tr;
            case FONT_CROX4T_TN ->
                fontSel = Fonts.u8g2_font_crox4t_tn;
            case FONT_CROX5HB_TF ->
                fontSel = Fonts.u8g2_font_crox5hb_tf;
            case FONT_CROX5HB_TR ->
                fontSel = Fonts.u8g2_font_crox5hb_tr;
            case FONT_CROX5HB_TN ->
                fontSel = Fonts.u8g2_font_crox5hb_tn;
            case FONT_CROX5H_TF ->
                fontSel = Fonts.u8g2_font_crox5h_tf;
            case FONT_CROX5H_TR ->
                fontSel = Fonts.u8g2_font_crox5h_tr;
            case FONT_CROX5H_TN ->
                fontSel = Fonts.u8g2_font_crox5h_tn;
            case FONT_CROX5TB_TF ->
                fontSel = Fonts.u8g2_font_crox5tb_tf;
            case FONT_CROX5TB_TR ->
                fontSel = Fonts.u8g2_font_crox5tb_tr;
            case FONT_CROX5TB_TN ->
                fontSel = Fonts.u8g2_font_crox5tb_tn;
            case FONT_CROX5T_TF ->
                fontSel = Fonts.u8g2_font_crox5t_tf;
            case FONT_CROX5T_TR ->
                fontSel = Fonts.u8g2_font_crox5t_tr;
            case FONT_CROX5T_TN ->
                fontSel = Fonts.u8g2_font_crox5t_tn;
            case FONT_CU12_TF ->
                fontSel = Fonts.u8g2_font_cu12_tf;
            case FONT_CU12_TR ->
                fontSel = Fonts.u8g2_font_cu12_tr;
            case FONT_CU12_TN ->
                fontSel = Fonts.u8g2_font_cu12_tn;
            case FONT_CU12_TE ->
                fontSel = Fonts.u8g2_font_cu12_te;
            case FONT_CU12_HF ->
                fontSel = Fonts.u8g2_font_cu12_hf;
            case FONT_CU12_HR ->
                fontSel = Fonts.u8g2_font_cu12_hr;
            case FONT_CU12_HN ->
                fontSel = Fonts.u8g2_font_cu12_hn;
            case FONT_CU12_HE ->
                fontSel = Fonts.u8g2_font_cu12_he;
            case FONT_CU12_MF ->
                fontSel = Fonts.u8g2_font_cu12_mf;
            case FONT_CU12_MR ->
                fontSel = Fonts.u8g2_font_cu12_mr;
            case FONT_CU12_MN ->
                fontSel = Fonts.u8g2_font_cu12_mn;
            case FONT_CU12_ME ->
                fontSel = Fonts.u8g2_font_cu12_me;
            case FONT_CU12_T_SYMBOLS ->
                fontSel = Fonts.u8g2_font_cu12_t_symbols;
            case FONT_CU12_H_SYMBOLS ->
                fontSel = Fonts.u8g2_font_cu12_h_symbols;
            case FONT_CU12_T_GREEK ->
                fontSel = Fonts.u8g2_font_cu12_t_greek;
            case FONT_CU12_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_cu12_t_cyrillic;
            case FONT_CU12_T_TIBETAN ->
                fontSel = Fonts.u8g2_font_cu12_t_tibetan;
            case FONT_CU12_T_HEBREW ->
                fontSel = Fonts.u8g2_font_cu12_t_hebrew;
            case FONT_CU12_T_ARABIC ->
                fontSel = Fonts.u8g2_font_cu12_t_arabic;
            case FONT_UNIFONT_TF ->
                fontSel = Fonts.u8g2_font_unifont_tf;
            case FONT_UNIFONT_TR ->
                fontSel = Fonts.u8g2_font_unifont_tr;
            case FONT_UNIFONT_TE ->
                fontSel = Fonts.u8g2_font_unifont_te;
            case FONT_UNIFONT_T_LATIN ->
                fontSel = Fonts.u8g2_font_unifont_t_latin;
            case FONT_UNIFONT_T_EXTENDED ->
                fontSel = Fonts.u8g2_font_unifont_t_extended;
            case FONT_UNIFONT_T_72_73 ->
                fontSel = Fonts.u8g2_font_unifont_t_72_73;
            case FONT_UNIFONT_T_0_72_73 ->
                fontSel = Fonts.u8g2_font_unifont_t_0_72_73;
            case FONT_UNIFONT_T_75 ->
                fontSel = Fonts.u8g2_font_unifont_t_75;
            case FONT_UNIFONT_T_0_75 ->
                fontSel = Fonts.u8g2_font_unifont_t_0_75;
            case FONT_UNIFONT_T_76 ->
                fontSel = Fonts.u8g2_font_unifont_t_76;
            case FONT_UNIFONT_T_0_76 ->
                fontSel = Fonts.u8g2_font_unifont_t_0_76;
            case FONT_UNIFONT_T_77 ->
                fontSel = Fonts.u8g2_font_unifont_t_77;
            case FONT_UNIFONT_T_0_77 ->
                fontSel = Fonts.u8g2_font_unifont_t_0_77;
            case FONT_UNIFONT_T_78_79 ->
                fontSel = Fonts.u8g2_font_unifont_t_78_79;
            case FONT_UNIFONT_T_0_78_79 ->
                fontSel = Fonts.u8g2_font_unifont_t_0_78_79;
            case FONT_UNIFONT_T_86 ->
                fontSel = Fonts.u8g2_font_unifont_t_86;
            case FONT_UNIFONT_T_0_86 ->
                fontSel = Fonts.u8g2_font_unifont_t_0_86;
            case FONT_UNIFONT_T_GREEK ->
                fontSel = Fonts.u8g2_font_unifont_t_greek;
            case FONT_UNIFONT_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_unifont_t_cyrillic;
            case FONT_UNIFONT_T_HEBREW ->
                fontSel = Fonts.u8g2_font_unifont_t_hebrew;
            case FONT_UNIFONT_T_BENGALI ->
                fontSel = Fonts.u8g2_font_unifont_t_bengali;
            case FONT_UNIFONT_T_TIBETAN ->
                fontSel = Fonts.u8g2_font_unifont_t_tibetan;
            case FONT_UNIFONT_T_URDU ->
                fontSel = Fonts.u8g2_font_unifont_t_urdu;
            case FONT_UNIFONT_T_POLISH ->
                fontSel = Fonts.u8g2_font_unifont_t_polish;
            case FONT_UNIFONT_T_DEVANAGARI ->
                fontSel = Fonts.u8g2_font_unifont_t_devanagari;
            case FONT_UNIFONT_T_MALAYALAM ->
                fontSel = Fonts.u8g2_font_unifont_t_malayalam;
            case FONT_UNIFONT_T_ARABIC ->
                fontSel = Fonts.u8g2_font_unifont_t_arabic;
            case FONT_UNIFONT_T_SYMBOLS ->
                fontSel = Fonts.u8g2_font_unifont_t_symbols;
            case FONT_UNIFONT_H_SYMBOLS ->
                fontSel = Fonts.u8g2_font_unifont_h_symbols;
            case FONT_UNIFONT_T_EMOTICONS ->
                fontSel = Fonts.u8g2_font_unifont_t_emoticons;
            case FONT_UNIFONT_T_ANIMALS ->
                fontSel = Fonts.u8g2_font_unifont_t_animals;
            case FONT_UNIFONT_T_DOMINO ->
                fontSel = Fonts.u8g2_font_unifont_t_domino;
            case FONT_UNIFONT_T_CARDS ->
                fontSel = Fonts.u8g2_font_unifont_t_cards;
            case FONT_UNIFONT_T_WEATHER ->
                fontSel = Fonts.u8g2_font_unifont_t_weather;
            case FONT_UNIFONT_T_CHINESE1 ->
                fontSel = Fonts.u8g2_font_unifont_t_chinese1;
            case FONT_UNIFONT_T_CHINESE2 ->
                fontSel = Fonts.u8g2_font_unifont_t_chinese2;
            case FONT_UNIFONT_T_CHINESE3 ->
                fontSel = Fonts.u8g2_font_unifont_t_chinese3;
            case FONT_UNIFONT_T_GB2312 ->
                fontSel = Fonts.u8g2_font_unifont_t_gb2312;
            case FONT_UNIFONT_T_GB2312A ->
                fontSel = Fonts.u8g2_font_unifont_t_gb2312a;
            case FONT_UNIFONT_T_GB2312B ->
                fontSel = Fonts.u8g2_font_unifont_t_gb2312b;
            case FONT_UNIFONT_T_JAPANESE1 ->
                fontSel = Fonts.u8g2_font_unifont_t_japanese1;
            case FONT_UNIFONT_T_JAPANESE2 ->
                fontSel = Fonts.u8g2_font_unifont_t_japanese2;
            case FONT_UNIFONT_T_JAPANESE3 ->
                fontSel = Fonts.u8g2_font_unifont_t_japanese3;
            case FONT_UNIFONT_T_KOREAN1 ->
                fontSel = Fonts.u8g2_font_unifont_t_korean1;
            case FONT_UNIFONT_T_KOREAN2 ->
                fontSel = Fonts.u8g2_font_unifont_t_korean2;
            case FONT_UNIFONT_T_VIETNAMESE1 ->
                fontSel = Fonts.u8g2_font_unifont_t_vietnamese1;
            case FONT_UNIFONT_T_VIETNAMESE2 ->
                fontSel = Fonts.u8g2_font_unifont_t_vietnamese2;
            case FONT_GB16ST_T_1 ->
                fontSel = Fonts.u8g2_font_gb16st_t_1;
            case FONT_GB16ST_T_2 ->
                fontSel = Fonts.u8g2_font_gb16st_t_2;
            case FONT_GB16ST_T_3 ->
                fontSel = Fonts.u8g2_font_gb16st_t_3;
            case FONT_GB24ST_T_1 ->
                fontSel = Fonts.u8g2_font_gb24st_t_1;
            case FONT_GB24ST_T_2 ->
                fontSel = Fonts.u8g2_font_gb24st_t_2;
            case FONT_GB24ST_T_3 ->
                fontSel = Fonts.u8g2_font_gb24st_t_3;
            case FONT_WQY12_T_CHINESE1 ->
                fontSel = Fonts.u8g2_font_wqy12_t_chinese1;
            case FONT_WQY12_T_CHINESE2 ->
                fontSel = Fonts.u8g2_font_wqy12_t_chinese2;
            case FONT_WQY12_T_CHINESE3 ->
                fontSel = Fonts.u8g2_font_wqy12_t_chinese3;
            case FONT_WQY12_T_GB2312 ->
                fontSel = Fonts.u8g2_font_wqy12_t_gb2312;
            case FONT_WQY12_T_GB2312A ->
                fontSel = Fonts.u8g2_font_wqy12_t_gb2312a;
            case FONT_WQY12_T_GB2312B ->
                fontSel = Fonts.u8g2_font_wqy12_t_gb2312b;
            case FONT_WQY13_T_CHINESE1 ->
                fontSel = Fonts.u8g2_font_wqy13_t_chinese1;
            case FONT_WQY13_T_CHINESE2 ->
                fontSel = Fonts.u8g2_font_wqy13_t_chinese2;
            case FONT_WQY13_T_CHINESE3 ->
                fontSel = Fonts.u8g2_font_wqy13_t_chinese3;
            case FONT_WQY13_T_GB2312 ->
                fontSel = Fonts.u8g2_font_wqy13_t_gb2312;
            case FONT_WQY13_T_GB2312A ->
                fontSel = Fonts.u8g2_font_wqy13_t_gb2312a;
            case FONT_WQY13_T_GB2312B ->
                fontSel = Fonts.u8g2_font_wqy13_t_gb2312b;
            case FONT_WQY14_T_CHINESE1 ->
                fontSel = Fonts.u8g2_font_wqy14_t_chinese1;
            case FONT_WQY14_T_CHINESE2 ->
                fontSel = Fonts.u8g2_font_wqy14_t_chinese2;
            case FONT_WQY14_T_CHINESE3 ->
                fontSel = Fonts.u8g2_font_wqy14_t_chinese3;
            case FONT_WQY14_T_GB2312 ->
                fontSel = Fonts.u8g2_font_wqy14_t_gb2312;
            case FONT_WQY14_T_GB2312A ->
                fontSel = Fonts.u8g2_font_wqy14_t_gb2312a;
            case FONT_WQY14_T_GB2312B ->
                fontSel = Fonts.u8g2_font_wqy14_t_gb2312b;
            case FONT_WQY15_T_CHINESE1 ->
                fontSel = Fonts.u8g2_font_wqy15_t_chinese1;
            case FONT_WQY15_T_CHINESE2 ->
                fontSel = Fonts.u8g2_font_wqy15_t_chinese2;
            case FONT_WQY15_T_CHINESE3 ->
                fontSel = Fonts.u8g2_font_wqy15_t_chinese3;
            case FONT_WQY15_T_GB2312 ->
                fontSel = Fonts.u8g2_font_wqy15_t_gb2312;
            case FONT_WQY15_T_GB2312A ->
                fontSel = Fonts.u8g2_font_wqy15_t_gb2312a;
            case FONT_WQY15_T_GB2312B ->
                fontSel = Fonts.u8g2_font_wqy15_t_gb2312b;
            case FONT_WQY16_T_CHINESE1 ->
                fontSel = Fonts.u8g2_font_wqy16_t_chinese1;
            case FONT_WQY16_T_CHINESE2 ->
                fontSel = Fonts.u8g2_font_wqy16_t_chinese2;
            case FONT_WQY16_T_CHINESE3 ->
                fontSel = Fonts.u8g2_font_wqy16_t_chinese3;
            case FONT_WQY16_T_GB2312 ->
                fontSel = Fonts.u8g2_font_wqy16_t_gb2312;
            case FONT_WQY16_T_GB2312A ->
                fontSel = Fonts.u8g2_font_wqy16_t_gb2312a;
            case FONT_WQY16_T_GB2312B ->
                fontSel = Fonts.u8g2_font_wqy16_t_gb2312b;
            case FONT_BOUTIQUE_BITMAP_7X7_TF ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_7x7_tf;
            case FONT_BOUTIQUE_BITMAP_7X7_TR ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_7x7_tr;
            case FONT_BOUTIQUE_BITMAP_7X7_TN ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_7x7_tn;
            case FONT_BOUTIQUE_BITMAP_7X7_TE ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_7x7_te;
            case FONT_BOUTIQUE_BITMAP_7X7_T_ALL ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_7x7_t_all;
            case FONT_BOUTIQUE_BITMAP_7X7_T_CHINESE1 ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_7x7_t_chinese1;
            case FONT_BOUTIQUE_BITMAP_7X7_T_CHINESE2 ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_7x7_t_chinese2;
            case FONT_BOUTIQUE_BITMAP_7X7_T_CHINESE3 ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_7x7_t_chinese3;
            case FONT_BOUTIQUE_BITMAP_7X7_T_GB2312 ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_7x7_t_gb2312;
            case FONT_BOUTIQUE_BITMAP_7X7_T_GB2312A ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_7x7_t_gb2312a;
            case FONT_BOUTIQUE_BITMAP_7X7_T_GB2312B ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_7x7_t_gb2312b;
            case FONT_BOUTIQUE_BITMAP_9X9_TF ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_tf;
            case FONT_BOUTIQUE_BITMAP_9X9_TR ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_tr;
            case FONT_BOUTIQUE_BITMAP_9X9_TN ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_tn;
            case FONT_BOUTIQUE_BITMAP_9X9_TE ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_te;
            case FONT_BOUTIQUE_BITMAP_9X9_T_ALL ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_t_all;
            case FONT_BOUTIQUE_BITMAP_9X9_T_CHINESE1 ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_t_chinese1;
            case FONT_BOUTIQUE_BITMAP_9X9_T_CHINESE2 ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_t_chinese2;
            case FONT_BOUTIQUE_BITMAP_9X9_T_CHINESE3 ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_t_chinese3;
            case FONT_BOUTIQUE_BITMAP_9X9_T_GB2312 ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_t_gb2312;
            case FONT_BOUTIQUE_BITMAP_9X9_T_GB2312A ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_t_gb2312a;
            case FONT_BOUTIQUE_BITMAP_9X9_T_GB2312B ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_t_gb2312b;
            case FONT_BOUTIQUE_BITMAP_9X9_BOLD_TF ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_bold_tf;
            case FONT_BOUTIQUE_BITMAP_9X9_BOLD_TR ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_bold_tr;
            case FONT_BOUTIQUE_BITMAP_9X9_BOLD_TN ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_bold_tn;
            case FONT_BOUTIQUE_BITMAP_9X9_BOLD_TE ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_bold_te;
            case FONT_BOUTIQUE_BITMAP_9X9_BOLD_T_ALL ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_bold_t_all;
            case FONT_BOUTIQUE_BITMAP_9X9_BOLD_T_CHINESE1 ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_bold_t_chinese1;
            case FONT_BOUTIQUE_BITMAP_9X9_BOLD_T_CHINESE2 ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_bold_t_chinese2;
            case FONT_BOUTIQUE_BITMAP_9X9_BOLD_T_CHINESE3 ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_bold_t_chinese3;
            case FONT_BOUTIQUE_BITMAP_9X9_BOLD_T_GB2312 ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_bold_t_gb2312;
            case FONT_BOUTIQUE_BITMAP_9X9_BOLD_T_GB2312A ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_bold_t_gb2312a;
            case FONT_BOUTIQUE_BITMAP_9X9_BOLD_T_GB2312B ->
                fontSel = Fonts.u8g2_font_boutique_bitmap_9x9_bold_t_gb2312b;
            case FONT_B10_T_JAPANESE1 ->
                fontSel = Fonts.u8g2_font_b10_t_japanese1;
            case FONT_B10_T_JAPANESE2 ->
                fontSel = Fonts.u8g2_font_b10_t_japanese2;
            case FONT_B10_B_T_JAPANESE1 ->
                fontSel = Fonts.u8g2_font_b10_b_t_japanese1;
            case FONT_B10_B_T_JAPANESE2 ->
                fontSel = Fonts.u8g2_font_b10_b_t_japanese2;
            case FONT_F10_T_JAPANESE1 ->
                fontSel = Fonts.u8g2_font_f10_t_japanese1;
            case FONT_F10_T_JAPANESE2 ->
                fontSel = Fonts.u8g2_font_f10_t_japanese2;
            case FONT_F10_B_T_JAPANESE1 ->
                fontSel = Fonts.u8g2_font_f10_b_t_japanese1;
            case FONT_F10_B_T_JAPANESE2 ->
                fontSel = Fonts.u8g2_font_f10_b_t_japanese2;
            case FONT_B12_T_JAPANESE1 ->
                fontSel = Fonts.u8g2_font_b12_t_japanese1;
            case FONT_B12_T_JAPANESE2 ->
                fontSel = Fonts.u8g2_font_b12_t_japanese2;
            case FONT_B12_T_JAPANESE3 ->
                fontSel = Fonts.u8g2_font_b12_t_japanese3;
            case FONT_B12_B_T_JAPANESE1 ->
                fontSel = Fonts.u8g2_font_b12_b_t_japanese1;
            case FONT_B12_B_T_JAPANESE2 ->
                fontSel = Fonts.u8g2_font_b12_b_t_japanese2;
            case FONT_B12_B_T_JAPANESE3 ->
                fontSel = Fonts.u8g2_font_b12_b_t_japanese3;
            case FONT_F12_T_JAPANESE1 ->
                fontSel = Fonts.u8g2_font_f12_t_japanese1;
            case FONT_F12_T_JAPANESE2 ->
                fontSel = Fonts.u8g2_font_f12_t_japanese2;
            case FONT_F12_B_T_JAPANESE1 ->
                fontSel = Fonts.u8g2_font_f12_b_t_japanese1;
            case FONT_F12_B_T_JAPANESE2 ->
                fontSel = Fonts.u8g2_font_f12_b_t_japanese2;
            case FONT_B16_T_JAPANESE1 ->
                fontSel = Fonts.u8g2_font_b16_t_japanese1;
            case FONT_B16_T_JAPANESE2 ->
                fontSel = Fonts.u8g2_font_b16_t_japanese2;
            case FONT_B16_T_JAPANESE3 ->
                fontSel = Fonts.u8g2_font_b16_t_japanese3;
            case FONT_B16_B_T_JAPANESE1 ->
                fontSel = Fonts.u8g2_font_b16_b_t_japanese1;
            case FONT_B16_B_T_JAPANESE2 ->
                fontSel = Fonts.u8g2_font_b16_b_t_japanese2;
            case FONT_B16_B_T_JAPANESE3 ->
                fontSel = Fonts.u8g2_font_b16_b_t_japanese3;
            case FONT_F16_T_JAPANESE1 ->
                fontSel = Fonts.u8g2_font_f16_t_japanese1;
            case FONT_F16_T_JAPANESE2 ->
                fontSel = Fonts.u8g2_font_f16_t_japanese2;
            case FONT_F16_B_T_JAPANESE1 ->
                fontSel = Fonts.u8g2_font_f16_b_t_japanese1;
            case FONT_F16_B_T_JAPANESE2 ->
                fontSel = Fonts.u8g2_font_f16_b_t_japanese2;
            case FONT_ARTOSSANS8_8R ->
                fontSel = Fonts.u8g2_font_artossans8_8r;
            case FONT_ARTOSSANS8_8N ->
                fontSel = Fonts.u8g2_font_artossans8_8n;
            case FONT_ARTOSSANS8_8U ->
                fontSel = Fonts.u8g2_font_artossans8_8u;
            case FONT_ARTOSSERIF8_8R ->
                fontSel = Fonts.u8g2_font_artosserif8_8r;
            case FONT_ARTOSSERIF8_8N ->
                fontSel = Fonts.u8g2_font_artosserif8_8n;
            case FONT_ARTOSSERIF8_8U ->
                fontSel = Fonts.u8g2_font_artosserif8_8u;
            case FONT_CHROMA48MEDIUM8_8R ->
                fontSel = Fonts.u8g2_font_chroma48medium8_8r;
            case FONT_CHROMA48MEDIUM8_8N ->
                fontSel = Fonts.u8g2_font_chroma48medium8_8n;
            case FONT_CHROMA48MEDIUM8_8U ->
                fontSel = Fonts.u8g2_font_chroma48medium8_8u;
            case FONT_SAIKYOSANSBOLD8_8N ->
                fontSel = Fonts.u8g2_font_saikyosansbold8_8n;
            case FONT_SAIKYOSANSBOLD8_8U ->
                fontSel = Fonts.u8g2_font_saikyosansbold8_8u;
            case FONT_TORUSSANSBOLD8_8R ->
                fontSel = Fonts.u8g2_font_torussansbold8_8r;
            case FONT_TORUSSANSBOLD8_8N ->
                fontSel = Fonts.u8g2_font_torussansbold8_8n;
            case FONT_TORUSSANSBOLD8_8U ->
                fontSel = Fonts.u8g2_font_torussansbold8_8u;
            case FONT_VICTORIABOLD8_8R ->
                fontSel = Fonts.u8g2_font_victoriabold8_8r;
            case FONT_VICTORIABOLD8_8N ->
                fontSel = Fonts.u8g2_font_victoriabold8_8n;
            case FONT_VICTORIABOLD8_8U ->
                fontSel = Fonts.u8g2_font_victoriabold8_8u;
            case FONT_VICTORIAMEDIUM8_8R ->
                fontSel = Fonts.u8g2_font_victoriamedium8_8r;
            case FONT_VICTORIAMEDIUM8_8N ->
                fontSel = Fonts.u8g2_font_victoriamedium8_8n;
            case FONT_VICTORIAMEDIUM8_8U ->
                fontSel = Fonts.u8g2_font_victoriamedium8_8u;
            case FONT_COURB08_TF ->
                fontSel = Fonts.u8g2_font_courB08_tf;
            case FONT_COURB08_TR ->
                fontSel = Fonts.u8g2_font_courB08_tr;
            case FONT_COURB08_TN ->
                fontSel = Fonts.u8g2_font_courB08_tn;
            case FONT_COURB10_TF ->
                fontSel = Fonts.u8g2_font_courB10_tf;
            case FONT_COURB10_TR ->
                fontSel = Fonts.u8g2_font_courB10_tr;
            case FONT_COURB10_TN ->
                fontSel = Fonts.u8g2_font_courB10_tn;
            case FONT_COURB12_TF ->
                fontSel = Fonts.u8g2_font_courB12_tf;
            case FONT_COURB12_TR ->
                fontSel = Fonts.u8g2_font_courB12_tr;
            case FONT_COURB12_TN ->
                fontSel = Fonts.u8g2_font_courB12_tn;
            case FONT_COURB14_TF ->
                fontSel = Fonts.u8g2_font_courB14_tf;
            case FONT_COURB14_TR ->
                fontSel = Fonts.u8g2_font_courB14_tr;
            case FONT_COURB14_TN ->
                fontSel = Fonts.u8g2_font_courB14_tn;
            case FONT_COURB18_TF ->
                fontSel = Fonts.u8g2_font_courB18_tf;
            case FONT_COURB18_TR ->
                fontSel = Fonts.u8g2_font_courB18_tr;
            case FONT_COURB18_TN ->
                fontSel = Fonts.u8g2_font_courB18_tn;
            case FONT_COURB24_TF ->
                fontSel = Fonts.u8g2_font_courB24_tf;
            case FONT_COURB24_TR ->
                fontSel = Fonts.u8g2_font_courB24_tr;
            case FONT_COURB24_TN ->
                fontSel = Fonts.u8g2_font_courB24_tn;
            case FONT_COURR08_TF ->
                fontSel = Fonts.u8g2_font_courR08_tf;
            case FONT_COURR08_TR ->
                fontSel = Fonts.u8g2_font_courR08_tr;
            case FONT_COURR08_TN ->
                fontSel = Fonts.u8g2_font_courR08_tn;
            case FONT_COURR10_TF ->
                fontSel = Fonts.u8g2_font_courR10_tf;
            case FONT_COURR10_TR ->
                fontSel = Fonts.u8g2_font_courR10_tr;
            case FONT_COURR10_TN ->
                fontSel = Fonts.u8g2_font_courR10_tn;
            case FONT_COURR12_TF ->
                fontSel = Fonts.u8g2_font_courR12_tf;
            case FONT_COURR12_TR ->
                fontSel = Fonts.u8g2_font_courR12_tr;
            case FONT_COURR12_TN ->
                fontSel = Fonts.u8g2_font_courR12_tn;
            case FONT_COURR14_TF ->
                fontSel = Fonts.u8g2_font_courR14_tf;
            case FONT_COURR14_TR ->
                fontSel = Fonts.u8g2_font_courR14_tr;
            case FONT_COURR14_TN ->
                fontSel = Fonts.u8g2_font_courR14_tn;
            case FONT_COURR18_TF ->
                fontSel = Fonts.u8g2_font_courR18_tf;
            case FONT_COURR18_TR ->
                fontSel = Fonts.u8g2_font_courR18_tr;
            case FONT_COURR18_TN ->
                fontSel = Fonts.u8g2_font_courR18_tn;
            case FONT_COURR24_TF ->
                fontSel = Fonts.u8g2_font_courR24_tf;
            case FONT_COURR24_TR ->
                fontSel = Fonts.u8g2_font_courR24_tr;
            case FONT_COURR24_TN ->
                fontSel = Fonts.u8g2_font_courR24_tn;
            case FONT_HELVB08_TF ->
                fontSel = Fonts.u8g2_font_helvB08_tf;
            case FONT_HELVB08_TR ->
                fontSel = Fonts.u8g2_font_helvB08_tr;
            case FONT_HELVB08_TN ->
                fontSel = Fonts.u8g2_font_helvB08_tn;
            case FONT_HELVB08_TE ->
                fontSel = Fonts.u8g2_font_helvB08_te;
            case FONT_HELVB10_TF ->
                fontSel = Fonts.u8g2_font_helvB10_tf;
            case FONT_HELVB10_TR ->
                fontSel = Fonts.u8g2_font_helvB10_tr;
            case FONT_HELVB10_TN ->
                fontSel = Fonts.u8g2_font_helvB10_tn;
            case FONT_HELVB10_TE ->
                fontSel = Fonts.u8g2_font_helvB10_te;
            case FONT_HELVB12_TF ->
                fontSel = Fonts.u8g2_font_helvB12_tf;
            case FONT_HELVB12_TR ->
                fontSel = Fonts.u8g2_font_helvB12_tr;
            case FONT_HELVB12_TN ->
                fontSel = Fonts.u8g2_font_helvB12_tn;
            case FONT_HELVB12_TE ->
                fontSel = Fonts.u8g2_font_helvB12_te;
            case FONT_HELVB14_TF ->
                fontSel = Fonts.u8g2_font_helvB14_tf;
            case FONT_HELVB14_TR ->
                fontSel = Fonts.u8g2_font_helvB14_tr;
            case FONT_HELVB14_TN ->
                fontSel = Fonts.u8g2_font_helvB14_tn;
            case FONT_HELVB14_TE ->
                fontSel = Fonts.u8g2_font_helvB14_te;
            case FONT_HELVB18_TF ->
                fontSel = Fonts.u8g2_font_helvB18_tf;
            case FONT_HELVB18_TR ->
                fontSel = Fonts.u8g2_font_helvB18_tr;
            case FONT_HELVB18_TN ->
                fontSel = Fonts.u8g2_font_helvB18_tn;
            case FONT_HELVB18_TE ->
                fontSel = Fonts.u8g2_font_helvB18_te;
            case FONT_HELVB24_TF ->
                fontSel = Fonts.u8g2_font_helvB24_tf;
            case FONT_HELVB24_TR ->
                fontSel = Fonts.u8g2_font_helvB24_tr;
            case FONT_HELVB24_TN ->
                fontSel = Fonts.u8g2_font_helvB24_tn;
            case FONT_HELVB24_TE ->
                fontSel = Fonts.u8g2_font_helvB24_te;
            case FONT_HELVR08_TF ->
                fontSel = Fonts.u8g2_font_helvR08_tf;
            case FONT_HELVR08_TR ->
                fontSel = Fonts.u8g2_font_helvR08_tr;
            case FONT_HELVR08_TN ->
                fontSel = Fonts.u8g2_font_helvR08_tn;
            case FONT_HELVR08_TE ->
                fontSel = Fonts.u8g2_font_helvR08_te;
            case FONT_HELVR10_TF ->
                fontSel = Fonts.u8g2_font_helvR10_tf;
            case FONT_HELVR10_TR ->
                fontSel = Fonts.u8g2_font_helvR10_tr;
            case FONT_HELVR10_TN ->
                fontSel = Fonts.u8g2_font_helvR10_tn;
            case FONT_HELVR10_TE ->
                fontSel = Fonts.u8g2_font_helvR10_te;
            case FONT_HELVR12_TF ->
                fontSel = Fonts.u8g2_font_helvR12_tf;
            case FONT_HELVR12_TR ->
                fontSel = Fonts.u8g2_font_helvR12_tr;
            case FONT_HELVR12_TN ->
                fontSel = Fonts.u8g2_font_helvR12_tn;
            case FONT_HELVR12_TE ->
                fontSel = Fonts.u8g2_font_helvR12_te;
            case FONT_HELVR14_TF ->
                fontSel = Fonts.u8g2_font_helvR14_tf;
            case FONT_HELVR14_TR ->
                fontSel = Fonts.u8g2_font_helvR14_tr;
            case FONT_HELVR14_TN ->
                fontSel = Fonts.u8g2_font_helvR14_tn;
            case FONT_HELVR14_TE ->
                fontSel = Fonts.u8g2_font_helvR14_te;
            case FONT_HELVR18_TF ->
                fontSel = Fonts.u8g2_font_helvR18_tf;
            case FONT_HELVR18_TR ->
                fontSel = Fonts.u8g2_font_helvR18_tr;
            case FONT_HELVR18_TN ->
                fontSel = Fonts.u8g2_font_helvR18_tn;
            case FONT_HELVR18_TE ->
                fontSel = Fonts.u8g2_font_helvR18_te;
            case FONT_HELVR24_TF ->
                fontSel = Fonts.u8g2_font_helvR24_tf;
            case FONT_HELVR24_TR ->
                fontSel = Fonts.u8g2_font_helvR24_tr;
            case FONT_HELVR24_TN ->
                fontSel = Fonts.u8g2_font_helvR24_tn;
            case FONT_HELVR24_TE ->
                fontSel = Fonts.u8g2_font_helvR24_te;
            case FONT_NCENB08_TF ->
                fontSel = Fonts.u8g2_font_ncenB08_tf;
            case FONT_NCENB08_TR ->
                fontSel = Fonts.u8g2_font_ncenB08_tr;
            case FONT_NCENB08_TN ->
                fontSel = Fonts.u8g2_font_ncenB08_tn;
            case FONT_NCENB08_TE ->
                fontSel = Fonts.u8g2_font_ncenB08_te;
            case FONT_NCENB10_TF ->
                fontSel = Fonts.u8g2_font_ncenB10_tf;
            case FONT_NCENB10_TR ->
                fontSel = Fonts.u8g2_font_ncenB10_tr;
            case FONT_NCENB10_TN ->
                fontSel = Fonts.u8g2_font_ncenB10_tn;
            case FONT_NCENB10_TE ->
                fontSel = Fonts.u8g2_font_ncenB10_te;
            case FONT_NCENB12_TF ->
                fontSel = Fonts.u8g2_font_ncenB12_tf;
            case FONT_NCENB12_TR ->
                fontSel = Fonts.u8g2_font_ncenB12_tr;
            case FONT_NCENB12_TN ->
                fontSel = Fonts.u8g2_font_ncenB12_tn;
            case FONT_NCENB12_TE ->
                fontSel = Fonts.u8g2_font_ncenB12_te;
            case FONT_NCENB14_TF ->
                fontSel = Fonts.u8g2_font_ncenB14_tf;
            case FONT_NCENB14_TR ->
                fontSel = Fonts.u8g2_font_ncenB14_tr;
            case FONT_NCENB14_TN ->
                fontSel = Fonts.u8g2_font_ncenB14_tn;
            case FONT_NCENB14_TE ->
                fontSel = Fonts.u8g2_font_ncenB14_te;
            case FONT_NCENB18_TF ->
                fontSel = Fonts.u8g2_font_ncenB18_tf;
            case FONT_NCENB18_TR ->
                fontSel = Fonts.u8g2_font_ncenB18_tr;
            case FONT_NCENB18_TN ->
                fontSel = Fonts.u8g2_font_ncenB18_tn;
            case FONT_NCENB18_TE ->
                fontSel = Fonts.u8g2_font_ncenB18_te;
            case FONT_NCENB24_TF ->
                fontSel = Fonts.u8g2_font_ncenB24_tf;
            case FONT_NCENB24_TR ->
                fontSel = Fonts.u8g2_font_ncenB24_tr;
            case FONT_NCENB24_TN ->
                fontSel = Fonts.u8g2_font_ncenB24_tn;
            case FONT_NCENB24_TE ->
                fontSel = Fonts.u8g2_font_ncenB24_te;
            case FONT_NCENR08_TF ->
                fontSel = Fonts.u8g2_font_ncenR08_tf;
            case FONT_NCENR08_TR ->
                fontSel = Fonts.u8g2_font_ncenR08_tr;
            case FONT_NCENR08_TN ->
                fontSel = Fonts.u8g2_font_ncenR08_tn;
            case FONT_NCENR08_TE ->
                fontSel = Fonts.u8g2_font_ncenR08_te;
            case FONT_NCENR10_TF ->
                fontSel = Fonts.u8g2_font_ncenR10_tf;
            case FONT_NCENR10_TR ->
                fontSel = Fonts.u8g2_font_ncenR10_tr;
            case FONT_NCENR10_TN ->
                fontSel = Fonts.u8g2_font_ncenR10_tn;
            case FONT_NCENR10_TE ->
                fontSel = Fonts.u8g2_font_ncenR10_te;
            case FONT_NCENR12_TF ->
                fontSel = Fonts.u8g2_font_ncenR12_tf;
            case FONT_NCENR12_TR ->
                fontSel = Fonts.u8g2_font_ncenR12_tr;
            case FONT_NCENR12_TN ->
                fontSel = Fonts.u8g2_font_ncenR12_tn;
            case FONT_NCENR12_TE ->
                fontSel = Fonts.u8g2_font_ncenR12_te;
            case FONT_NCENR14_TF ->
                fontSel = Fonts.u8g2_font_ncenR14_tf;
            case FONT_NCENR14_TR ->
                fontSel = Fonts.u8g2_font_ncenR14_tr;
            case FONT_NCENR14_TN ->
                fontSel = Fonts.u8g2_font_ncenR14_tn;
            case FONT_NCENR14_TE ->
                fontSel = Fonts.u8g2_font_ncenR14_te;
            case FONT_NCENR18_TF ->
                fontSel = Fonts.u8g2_font_ncenR18_tf;
            case FONT_NCENR18_TR ->
                fontSel = Fonts.u8g2_font_ncenR18_tr;
            case FONT_NCENR18_TN ->
                fontSel = Fonts.u8g2_font_ncenR18_tn;
            case FONT_NCENR18_TE ->
                fontSel = Fonts.u8g2_font_ncenR18_te;
            case FONT_NCENR24_TF ->
                fontSel = Fonts.u8g2_font_ncenR24_tf;
            case FONT_NCENR24_TR ->
                fontSel = Fonts.u8g2_font_ncenR24_tr;
            case FONT_NCENR24_TN ->
                fontSel = Fonts.u8g2_font_ncenR24_tn;
            case FONT_NCENR24_TE ->
                fontSel = Fonts.u8g2_font_ncenR24_te;
            case FONT_TIMB08_TF ->
                fontSel = Fonts.u8g2_font_timB08_tf;
            case FONT_TIMB08_TR ->
                fontSel = Fonts.u8g2_font_timB08_tr;
            case FONT_TIMB08_TN ->
                fontSel = Fonts.u8g2_font_timB08_tn;
            case FONT_TIMB10_TF ->
                fontSel = Fonts.u8g2_font_timB10_tf;
            case FONT_TIMB10_TR ->
                fontSel = Fonts.u8g2_font_timB10_tr;
            case FONT_TIMB10_TN ->
                fontSel = Fonts.u8g2_font_timB10_tn;
            case FONT_TIMB12_TF ->
                fontSel = Fonts.u8g2_font_timB12_tf;
            case FONT_TIMB12_TR ->
                fontSel = Fonts.u8g2_font_timB12_tr;
            case FONT_TIMB12_TN ->
                fontSel = Fonts.u8g2_font_timB12_tn;
            case FONT_TIMB14_TF ->
                fontSel = Fonts.u8g2_font_timB14_tf;
            case FONT_TIMB14_TR ->
                fontSel = Fonts.u8g2_font_timB14_tr;
            case FONT_TIMB14_TN ->
                fontSel = Fonts.u8g2_font_timB14_tn;
            case FONT_TIMB18_TF ->
                fontSel = Fonts.u8g2_font_timB18_tf;
            case FONT_TIMB18_TR ->
                fontSel = Fonts.u8g2_font_timB18_tr;
            case FONT_TIMB18_TN ->
                fontSel = Fonts.u8g2_font_timB18_tn;
            case FONT_TIMB24_TF ->
                fontSel = Fonts.u8g2_font_timB24_tf;
            case FONT_TIMB24_TR ->
                fontSel = Fonts.u8g2_font_timB24_tr;
            case FONT_TIMB24_TN ->
                fontSel = Fonts.u8g2_font_timB24_tn;
            case FONT_TIMR08_TF ->
                fontSel = Fonts.u8g2_font_timR08_tf;
            case FONT_TIMR08_TR ->
                fontSel = Fonts.u8g2_font_timR08_tr;
            case FONT_TIMR08_TN ->
                fontSel = Fonts.u8g2_font_timR08_tn;
            case FONT_TIMR10_TF ->
                fontSel = Fonts.u8g2_font_timR10_tf;
            case FONT_TIMR10_TR ->
                fontSel = Fonts.u8g2_font_timR10_tr;
            case FONT_TIMR10_TN ->
                fontSel = Fonts.u8g2_font_timR10_tn;
            case FONT_TIMR12_TF ->
                fontSel = Fonts.u8g2_font_timR12_tf;
            case FONT_TIMR12_TR ->
                fontSel = Fonts.u8g2_font_timR12_tr;
            case FONT_TIMR12_TN ->
                fontSel = Fonts.u8g2_font_timR12_tn;
            case FONT_TIMR14_TF ->
                fontSel = Fonts.u8g2_font_timR14_tf;
            case FONT_TIMR14_TR ->
                fontSel = Fonts.u8g2_font_timR14_tr;
            case FONT_TIMR14_TN ->
                fontSel = Fonts.u8g2_font_timR14_tn;
            case FONT_TIMR18_TF ->
                fontSel = Fonts.u8g2_font_timR18_tf;
            case FONT_TIMR18_TR ->
                fontSel = Fonts.u8g2_font_timR18_tr;
            case FONT_TIMR18_TN ->
                fontSel = Fonts.u8g2_font_timR18_tn;
            case FONT_TIMR24_TF ->
                fontSel = Fonts.u8g2_font_timR24_tf;
            case FONT_TIMR24_TR ->
                fontSel = Fonts.u8g2_font_timR24_tr;
            case FONT_TIMR24_TN ->
                fontSel = Fonts.u8g2_font_timR24_tn;
            case FONT_LUBB08_TF ->
                fontSel = Fonts.u8g2_font_lubB08_tf;
            case FONT_LUBB08_TR ->
                fontSel = Fonts.u8g2_font_lubB08_tr;
            case FONT_LUBB08_TN ->
                fontSel = Fonts.u8g2_font_lubB08_tn;
            case FONT_LUBB08_TE ->
                fontSel = Fonts.u8g2_font_lubB08_te;
            case FONT_LUBB10_TF ->
                fontSel = Fonts.u8g2_font_lubB10_tf;
            case FONT_LUBB10_TR ->
                fontSel = Fonts.u8g2_font_lubB10_tr;
            case FONT_LUBB10_TN ->
                fontSel = Fonts.u8g2_font_lubB10_tn;
            case FONT_LUBB10_TE ->
                fontSel = Fonts.u8g2_font_lubB10_te;
            case FONT_LUBB12_TF ->
                fontSel = Fonts.u8g2_font_lubB12_tf;
            case FONT_LUBB12_TR ->
                fontSel = Fonts.u8g2_font_lubB12_tr;
            case FONT_LUBB12_TN ->
                fontSel = Fonts.u8g2_font_lubB12_tn;
            case FONT_LUBB12_TE ->
                fontSel = Fonts.u8g2_font_lubB12_te;
            case FONT_LUBB14_TF ->
                fontSel = Fonts.u8g2_font_lubB14_tf;
            case FONT_LUBB14_TR ->
                fontSel = Fonts.u8g2_font_lubB14_tr;
            case FONT_LUBB14_TN ->
                fontSel = Fonts.u8g2_font_lubB14_tn;
            case FONT_LUBB14_TE ->
                fontSel = Fonts.u8g2_font_lubB14_te;
            case FONT_LUBB18_TF ->
                fontSel = Fonts.u8g2_font_lubB18_tf;
            case FONT_LUBB18_TR ->
                fontSel = Fonts.u8g2_font_lubB18_tr;
            case FONT_LUBB18_TN ->
                fontSel = Fonts.u8g2_font_lubB18_tn;
            case FONT_LUBB18_TE ->
                fontSel = Fonts.u8g2_font_lubB18_te;
            case FONT_LUBB19_TF ->
                fontSel = Fonts.u8g2_font_lubB19_tf;
            case FONT_LUBB19_TR ->
                fontSel = Fonts.u8g2_font_lubB19_tr;
            case FONT_LUBB19_TN ->
                fontSel = Fonts.u8g2_font_lubB19_tn;
            case FONT_LUBB19_TE ->
                fontSel = Fonts.u8g2_font_lubB19_te;
            case FONT_LUBB24_TF ->
                fontSel = Fonts.u8g2_font_lubB24_tf;
            case FONT_LUBB24_TR ->
                fontSel = Fonts.u8g2_font_lubB24_tr;
            case FONT_LUBB24_TN ->
                fontSel = Fonts.u8g2_font_lubB24_tn;
            case FONT_LUBB24_TE ->
                fontSel = Fonts.u8g2_font_lubB24_te;
            case FONT_LUBBI08_TF ->
                fontSel = Fonts.u8g2_font_lubBI08_tf;
            case FONT_LUBBI08_TR ->
                fontSel = Fonts.u8g2_font_lubBI08_tr;
            case FONT_LUBBI08_TN ->
                fontSel = Fonts.u8g2_font_lubBI08_tn;
            case FONT_LUBBI08_TE ->
                fontSel = Fonts.u8g2_font_lubBI08_te;
            case FONT_LUBBI10_TF ->
                fontSel = Fonts.u8g2_font_lubBI10_tf;
            case FONT_LUBBI10_TR ->
                fontSel = Fonts.u8g2_font_lubBI10_tr;
            case FONT_LUBBI10_TN ->
                fontSel = Fonts.u8g2_font_lubBI10_tn;
            case FONT_LUBBI10_TE ->
                fontSel = Fonts.u8g2_font_lubBI10_te;
            case FONT_LUBBI12_TF ->
                fontSel = Fonts.u8g2_font_lubBI12_tf;
            case FONT_LUBBI12_TR ->
                fontSel = Fonts.u8g2_font_lubBI12_tr;
            case FONT_LUBBI12_TN ->
                fontSel = Fonts.u8g2_font_lubBI12_tn;
            case FONT_LUBBI12_TE ->
                fontSel = Fonts.u8g2_font_lubBI12_te;
            case FONT_LUBBI14_TF ->
                fontSel = Fonts.u8g2_font_lubBI14_tf;
            case FONT_LUBBI14_TR ->
                fontSel = Fonts.u8g2_font_lubBI14_tr;
            case FONT_LUBBI14_TN ->
                fontSel = Fonts.u8g2_font_lubBI14_tn;
            case FONT_LUBBI14_TE ->
                fontSel = Fonts.u8g2_font_lubBI14_te;
            case FONT_LUBBI18_TF ->
                fontSel = Fonts.u8g2_font_lubBI18_tf;
            case FONT_LUBBI18_TR ->
                fontSel = Fonts.u8g2_font_lubBI18_tr;
            case FONT_LUBBI18_TN ->
                fontSel = Fonts.u8g2_font_lubBI18_tn;
            case FONT_LUBBI18_TE ->
                fontSel = Fonts.u8g2_font_lubBI18_te;
            case FONT_LUBBI19_TF ->
                fontSel = Fonts.u8g2_font_lubBI19_tf;
            case FONT_LUBBI19_TR ->
                fontSel = Fonts.u8g2_font_lubBI19_tr;
            case FONT_LUBBI19_TN ->
                fontSel = Fonts.u8g2_font_lubBI19_tn;
            case FONT_LUBBI19_TE ->
                fontSel = Fonts.u8g2_font_lubBI19_te;
            case FONT_LUBBI24_TF ->
                fontSel = Fonts.u8g2_font_lubBI24_tf;
            case FONT_LUBBI24_TR ->
                fontSel = Fonts.u8g2_font_lubBI24_tr;
            case FONT_LUBBI24_TN ->
                fontSel = Fonts.u8g2_font_lubBI24_tn;
            case FONT_LUBBI24_TE ->
                fontSel = Fonts.u8g2_font_lubBI24_te;
            case FONT_LUBI08_TF ->
                fontSel = Fonts.u8g2_font_lubI08_tf;
            case FONT_LUBI08_TR ->
                fontSel = Fonts.u8g2_font_lubI08_tr;
            case FONT_LUBI08_TN ->
                fontSel = Fonts.u8g2_font_lubI08_tn;
            case FONT_LUBI08_TE ->
                fontSel = Fonts.u8g2_font_lubI08_te;
            case FONT_LUBI10_TF ->
                fontSel = Fonts.u8g2_font_lubI10_tf;
            case FONT_LUBI10_TR ->
                fontSel = Fonts.u8g2_font_lubI10_tr;
            case FONT_LUBI10_TN ->
                fontSel = Fonts.u8g2_font_lubI10_tn;
            case FONT_LUBI10_TE ->
                fontSel = Fonts.u8g2_font_lubI10_te;
            case FONT_LUBI12_TF ->
                fontSel = Fonts.u8g2_font_lubI12_tf;
            case FONT_LUBI12_TR ->
                fontSel = Fonts.u8g2_font_lubI12_tr;
            case FONT_LUBI12_TN ->
                fontSel = Fonts.u8g2_font_lubI12_tn;
            case FONT_LUBI12_TE ->
                fontSel = Fonts.u8g2_font_lubI12_te;
            case FONT_LUBI14_TF ->
                fontSel = Fonts.u8g2_font_lubI14_tf;
            case FONT_LUBI14_TR ->
                fontSel = Fonts.u8g2_font_lubI14_tr;
            case FONT_LUBI14_TN ->
                fontSel = Fonts.u8g2_font_lubI14_tn;
            case FONT_LUBI14_TE ->
                fontSel = Fonts.u8g2_font_lubI14_te;
            case FONT_LUBI18_TF ->
                fontSel = Fonts.u8g2_font_lubI18_tf;
            case FONT_LUBI18_TR ->
                fontSel = Fonts.u8g2_font_lubI18_tr;
            case FONT_LUBI18_TN ->
                fontSel = Fonts.u8g2_font_lubI18_tn;
            case FONT_LUBI18_TE ->
                fontSel = Fonts.u8g2_font_lubI18_te;
            case FONT_LUBI19_TF ->
                fontSel = Fonts.u8g2_font_lubI19_tf;
            case FONT_LUBI19_TR ->
                fontSel = Fonts.u8g2_font_lubI19_tr;
            case FONT_LUBI19_TN ->
                fontSel = Fonts.u8g2_font_lubI19_tn;
            case FONT_LUBI19_TE ->
                fontSel = Fonts.u8g2_font_lubI19_te;
            case FONT_LUBI24_TF ->
                fontSel = Fonts.u8g2_font_lubI24_tf;
            case FONT_LUBI24_TR ->
                fontSel = Fonts.u8g2_font_lubI24_tr;
            case FONT_LUBI24_TN ->
                fontSel = Fonts.u8g2_font_lubI24_tn;
            case FONT_LUBI24_TE ->
                fontSel = Fonts.u8g2_font_lubI24_te;
            case FONT_LUBIS08_TF ->
                fontSel = Fonts.u8g2_font_luBIS08_tf;
            case FONT_LUBIS08_TR ->
                fontSel = Fonts.u8g2_font_luBIS08_tr;
            case FONT_LUBIS08_TN ->
                fontSel = Fonts.u8g2_font_luBIS08_tn;
            case FONT_LUBIS08_TE ->
                fontSel = Fonts.u8g2_font_luBIS08_te;
            case FONT_LUBIS10_TF ->
                fontSel = Fonts.u8g2_font_luBIS10_tf;
            case FONT_LUBIS10_TR ->
                fontSel = Fonts.u8g2_font_luBIS10_tr;
            case FONT_LUBIS10_TN ->
                fontSel = Fonts.u8g2_font_luBIS10_tn;
            case FONT_LUBIS10_TE ->
                fontSel = Fonts.u8g2_font_luBIS10_te;
            case FONT_LUBIS12_TF ->
                fontSel = Fonts.u8g2_font_luBIS12_tf;
            case FONT_LUBIS12_TR ->
                fontSel = Fonts.u8g2_font_luBIS12_tr;
            case FONT_LUBIS12_TN ->
                fontSel = Fonts.u8g2_font_luBIS12_tn;
            case FONT_LUBIS12_TE ->
                fontSel = Fonts.u8g2_font_luBIS12_te;
            case FONT_LUBIS14_TF ->
                fontSel = Fonts.u8g2_font_luBIS14_tf;
            case FONT_LUBIS14_TR ->
                fontSel = Fonts.u8g2_font_luBIS14_tr;
            case FONT_LUBIS14_TN ->
                fontSel = Fonts.u8g2_font_luBIS14_tn;
            case FONT_LUBIS14_TE ->
                fontSel = Fonts.u8g2_font_luBIS14_te;
            case FONT_LUBIS18_TF ->
                fontSel = Fonts.u8g2_font_luBIS18_tf;
            case FONT_LUBIS18_TR ->
                fontSel = Fonts.u8g2_font_luBIS18_tr;
            case FONT_LUBIS18_TN ->
                fontSel = Fonts.u8g2_font_luBIS18_tn;
            case FONT_LUBIS18_TE ->
                fontSel = Fonts.u8g2_font_luBIS18_te;
            case FONT_LUBIS19_TF ->
                fontSel = Fonts.u8g2_font_luBIS19_tf;
            case FONT_LUBIS19_TR ->
                fontSel = Fonts.u8g2_font_luBIS19_tr;
            case FONT_LUBIS19_TN ->
                fontSel = Fonts.u8g2_font_luBIS19_tn;
            case FONT_LUBIS19_TE ->
                fontSel = Fonts.u8g2_font_luBIS19_te;
            case FONT_LUBIS24_TF ->
                fontSel = Fonts.u8g2_font_luBIS24_tf;
            case FONT_LUBIS24_TR ->
                fontSel = Fonts.u8g2_font_luBIS24_tr;
            case FONT_LUBIS24_TN ->
                fontSel = Fonts.u8g2_font_luBIS24_tn;
            case FONT_LUBIS24_TE ->
                fontSel = Fonts.u8g2_font_luBIS24_te;
            case FONT_LUBR08_TF ->
                fontSel = Fonts.u8g2_font_lubR08_tf;
            case FONT_LUBR08_TR ->
                fontSel = Fonts.u8g2_font_lubR08_tr;
            case FONT_LUBR08_TN ->
                fontSel = Fonts.u8g2_font_lubR08_tn;
            case FONT_LUBR08_TE ->
                fontSel = Fonts.u8g2_font_lubR08_te;
            case FONT_LUBR10_TF ->
                fontSel = Fonts.u8g2_font_lubR10_tf;
            case FONT_LUBR10_TR ->
                fontSel = Fonts.u8g2_font_lubR10_tr;
            case FONT_LUBR10_TN ->
                fontSel = Fonts.u8g2_font_lubR10_tn;
            case FONT_LUBR10_TE ->
                fontSel = Fonts.u8g2_font_lubR10_te;
            case FONT_LUBR12_TF ->
                fontSel = Fonts.u8g2_font_lubR12_tf;
            case FONT_LUBR12_TR ->
                fontSel = Fonts.u8g2_font_lubR12_tr;
            case FONT_LUBR12_TN ->
                fontSel = Fonts.u8g2_font_lubR12_tn;
            case FONT_LUBR12_TE ->
                fontSel = Fonts.u8g2_font_lubR12_te;
            case FONT_LUBR14_TF ->
                fontSel = Fonts.u8g2_font_lubR14_tf;
            case FONT_LUBR14_TR ->
                fontSel = Fonts.u8g2_font_lubR14_tr;
            case FONT_LUBR14_TN ->
                fontSel = Fonts.u8g2_font_lubR14_tn;
            case FONT_LUBR14_TE ->
                fontSel = Fonts.u8g2_font_lubR14_te;
            case FONT_LUBR18_TF ->
                fontSel = Fonts.u8g2_font_lubR18_tf;
            case FONT_LUBR18_TR ->
                fontSel = Fonts.u8g2_font_lubR18_tr;
            case FONT_LUBR18_TN ->
                fontSel = Fonts.u8g2_font_lubR18_tn;
            case FONT_LUBR18_TE ->
                fontSel = Fonts.u8g2_font_lubR18_te;
            case FONT_LUBR19_TF ->
                fontSel = Fonts.u8g2_font_lubR19_tf;
            case FONT_LUBR19_TR ->
                fontSel = Fonts.u8g2_font_lubR19_tr;
            case FONT_LUBR19_TN ->
                fontSel = Fonts.u8g2_font_lubR19_tn;
            case FONT_LUBR19_TE ->
                fontSel = Fonts.u8g2_font_lubR19_te;
            case FONT_LUBR24_TF ->
                fontSel = Fonts.u8g2_font_lubR24_tf;
            case FONT_LUBR24_TR ->
                fontSel = Fonts.u8g2_font_lubR24_tr;
            case FONT_LUBR24_TN ->
                fontSel = Fonts.u8g2_font_lubR24_tn;
            case FONT_LUBR24_TE ->
                fontSel = Fonts.u8g2_font_lubR24_te;
            case FONT_LUBS08_TF ->
                fontSel = Fonts.u8g2_font_luBS08_tf;
            case FONT_LUBS08_TR ->
                fontSel = Fonts.u8g2_font_luBS08_tr;
            case FONT_LUBS08_TN ->
                fontSel = Fonts.u8g2_font_luBS08_tn;
            case FONT_LUBS08_TE ->
                fontSel = Fonts.u8g2_font_luBS08_te;
            case FONT_LUBS10_TF ->
                fontSel = Fonts.u8g2_font_luBS10_tf;
            case FONT_LUBS10_TR ->
                fontSel = Fonts.u8g2_font_luBS10_tr;
            case FONT_LUBS10_TN ->
                fontSel = Fonts.u8g2_font_luBS10_tn;
            case FONT_LUBS10_TE ->
                fontSel = Fonts.u8g2_font_luBS10_te;
            case FONT_LUBS12_TF ->
                fontSel = Fonts.u8g2_font_luBS12_tf;
            case FONT_LUBS12_TR ->
                fontSel = Fonts.u8g2_font_luBS12_tr;
            case FONT_LUBS12_TN ->
                fontSel = Fonts.u8g2_font_luBS12_tn;
            case FONT_LUBS12_TE ->
                fontSel = Fonts.u8g2_font_luBS12_te;
            case FONT_LUBS14_TF ->
                fontSel = Fonts.u8g2_font_luBS14_tf;
            case FONT_LUBS14_TR ->
                fontSel = Fonts.u8g2_font_luBS14_tr;
            case FONT_LUBS14_TN ->
                fontSel = Fonts.u8g2_font_luBS14_tn;
            case FONT_LUBS14_TE ->
                fontSel = Fonts.u8g2_font_luBS14_te;
            case FONT_LUBS18_TF ->
                fontSel = Fonts.u8g2_font_luBS18_tf;
            case FONT_LUBS18_TR ->
                fontSel = Fonts.u8g2_font_luBS18_tr;
            case FONT_LUBS18_TN ->
                fontSel = Fonts.u8g2_font_luBS18_tn;
            case FONT_LUBS18_TE ->
                fontSel = Fonts.u8g2_font_luBS18_te;
            case FONT_LUBS19_TF ->
                fontSel = Fonts.u8g2_font_luBS19_tf;
            case FONT_LUBS19_TR ->
                fontSel = Fonts.u8g2_font_luBS19_tr;
            case FONT_LUBS19_TN ->
                fontSel = Fonts.u8g2_font_luBS19_tn;
            case FONT_LUBS19_TE ->
                fontSel = Fonts.u8g2_font_luBS19_te;
            case FONT_LUBS24_TF ->
                fontSel = Fonts.u8g2_font_luBS24_tf;
            case FONT_LUBS24_TR ->
                fontSel = Fonts.u8g2_font_luBS24_tr;
            case FONT_LUBS24_TN ->
                fontSel = Fonts.u8g2_font_luBS24_tn;
            case FONT_LUBS24_TE ->
                fontSel = Fonts.u8g2_font_luBS24_te;
            case FONT_LUIS08_TF ->
                fontSel = Fonts.u8g2_font_luIS08_tf;
            case FONT_LUIS08_TR ->
                fontSel = Fonts.u8g2_font_luIS08_tr;
            case FONT_LUIS08_TN ->
                fontSel = Fonts.u8g2_font_luIS08_tn;
            case FONT_LUIS08_TE ->
                fontSel = Fonts.u8g2_font_luIS08_te;
            case FONT_LUIS10_TF ->
                fontSel = Fonts.u8g2_font_luIS10_tf;
            case FONT_LUIS10_TR ->
                fontSel = Fonts.u8g2_font_luIS10_tr;
            case FONT_LUIS10_TN ->
                fontSel = Fonts.u8g2_font_luIS10_tn;
            case FONT_LUIS10_TE ->
                fontSel = Fonts.u8g2_font_luIS10_te;
            case FONT_LUIS12_TF ->
                fontSel = Fonts.u8g2_font_luIS12_tf;
            case FONT_LUIS12_TR ->
                fontSel = Fonts.u8g2_font_luIS12_tr;
            case FONT_LUIS12_TN ->
                fontSel = Fonts.u8g2_font_luIS12_tn;
            case FONT_LUIS12_TE ->
                fontSel = Fonts.u8g2_font_luIS12_te;
            case FONT_LUIS14_TF ->
                fontSel = Fonts.u8g2_font_luIS14_tf;
            case FONT_LUIS14_TR ->
                fontSel = Fonts.u8g2_font_luIS14_tr;
            case FONT_LUIS14_TN ->
                fontSel = Fonts.u8g2_font_luIS14_tn;
            case FONT_LUIS14_TE ->
                fontSel = Fonts.u8g2_font_luIS14_te;
            case FONT_LUIS18_TF ->
                fontSel = Fonts.u8g2_font_luIS18_tf;
            case FONT_LUIS18_TR ->
                fontSel = Fonts.u8g2_font_luIS18_tr;
            case FONT_LUIS18_TN ->
                fontSel = Fonts.u8g2_font_luIS18_tn;
            case FONT_LUIS18_TE ->
                fontSel = Fonts.u8g2_font_luIS18_te;
            case FONT_LUIS19_TF ->
                fontSel = Fonts.u8g2_font_luIS19_tf;
            case FONT_LUIS19_TR ->
                fontSel = Fonts.u8g2_font_luIS19_tr;
            case FONT_LUIS19_TN ->
                fontSel = Fonts.u8g2_font_luIS19_tn;
            case FONT_LUIS19_TE ->
                fontSel = Fonts.u8g2_font_luIS19_te;
            case FONT_LUIS24_TF ->
                fontSel = Fonts.u8g2_font_luIS24_tf;
            case FONT_LUIS24_TR ->
                fontSel = Fonts.u8g2_font_luIS24_tr;
            case FONT_LUIS24_TN ->
                fontSel = Fonts.u8g2_font_luIS24_tn;
            case FONT_LUIS24_TE ->
                fontSel = Fonts.u8g2_font_luIS24_te;
            case FONT_LURS08_TF ->
                fontSel = Fonts.u8g2_font_luRS08_tf;
            case FONT_LURS08_TR ->
                fontSel = Fonts.u8g2_font_luRS08_tr;
            case FONT_LURS08_TN ->
                fontSel = Fonts.u8g2_font_luRS08_tn;
            case FONT_LURS08_TE ->
                fontSel = Fonts.u8g2_font_luRS08_te;
            case FONT_LURS10_TF ->
                fontSel = Fonts.u8g2_font_luRS10_tf;
            case FONT_LURS10_TR ->
                fontSel = Fonts.u8g2_font_luRS10_tr;
            case FONT_LURS10_TN ->
                fontSel = Fonts.u8g2_font_luRS10_tn;
            case FONT_LURS10_TE ->
                fontSel = Fonts.u8g2_font_luRS10_te;
            case FONT_LURS12_TF ->
                fontSel = Fonts.u8g2_font_luRS12_tf;
            case FONT_LURS12_TR ->
                fontSel = Fonts.u8g2_font_luRS12_tr;
            case FONT_LURS12_TN ->
                fontSel = Fonts.u8g2_font_luRS12_tn;
            case FONT_LURS12_TE ->
                fontSel = Fonts.u8g2_font_luRS12_te;
            case FONT_LURS14_TF ->
                fontSel = Fonts.u8g2_font_luRS14_tf;
            case FONT_LURS14_TR ->
                fontSel = Fonts.u8g2_font_luRS14_tr;
            case FONT_LURS14_TN ->
                fontSel = Fonts.u8g2_font_luRS14_tn;
            case FONT_LURS14_TE ->
                fontSel = Fonts.u8g2_font_luRS14_te;
            case FONT_LURS18_TF ->
                fontSel = Fonts.u8g2_font_luRS18_tf;
            case FONT_LURS18_TR ->
                fontSel = Fonts.u8g2_font_luRS18_tr;
            case FONT_LURS18_TN ->
                fontSel = Fonts.u8g2_font_luRS18_tn;
            case FONT_LURS18_TE ->
                fontSel = Fonts.u8g2_font_luRS18_te;
            case FONT_LURS19_TF ->
                fontSel = Fonts.u8g2_font_luRS19_tf;
            case FONT_LURS19_TR ->
                fontSel = Fonts.u8g2_font_luRS19_tr;
            case FONT_LURS19_TN ->
                fontSel = Fonts.u8g2_font_luRS19_tn;
            case FONT_LURS19_TE ->
                fontSel = Fonts.u8g2_font_luRS19_te;
            case FONT_LURS24_TF ->
                fontSel = Fonts.u8g2_font_luRS24_tf;
            case FONT_LURS24_TR ->
                fontSel = Fonts.u8g2_font_luRS24_tr;
            case FONT_LURS24_TN ->
                fontSel = Fonts.u8g2_font_luRS24_tn;
            case FONT_LURS24_TE ->
                fontSel = Fonts.u8g2_font_luRS24_te;
            case FONT_BABY_TF ->
                fontSel = Fonts.u8g2_font_baby_tf;
            case FONT_BABY_TR ->
                fontSel = Fonts.u8g2_font_baby_tr;
            case FONT_BABY_TN ->
                fontSel = Fonts.u8g2_font_baby_tn;
            case FONT_BLIPFEST_07_TR ->
                fontSel = Fonts.u8g2_font_blipfest_07_tr;
            case FONT_BLIPFEST_07_TN ->
                fontSel = Fonts.u8g2_font_blipfest_07_tn;
            case FONT_CHIKITA_TF ->
                fontSel = Fonts.u8g2_font_chikita_tf;
            case FONT_CHIKITA_TR ->
                fontSel = Fonts.u8g2_font_chikita_tr;
            case FONT_CHIKITA_TN ->
                fontSel = Fonts.u8g2_font_chikita_tn;
            case FONT_LUCASFONT_ALTERNATE_TF ->
                fontSel = Fonts.u8g2_font_lucasfont_alternate_tf;
            case FONT_LUCASFONT_ALTERNATE_TR ->
                fontSel = Fonts.u8g2_font_lucasfont_alternate_tr;
            case FONT_LUCASFONT_ALTERNATE_TN ->
                fontSel = Fonts.u8g2_font_lucasfont_alternate_tn;
            case FONT_P01TYPE_TF ->
                fontSel = Fonts.u8g2_font_p01type_tf;
            case FONT_P01TYPE_TR ->
                fontSel = Fonts.u8g2_font_p01type_tr;
            case FONT_P01TYPE_TN ->
                fontSel = Fonts.u8g2_font_p01type_tn;
            case FONT_PIXELLE_MICRO_TR ->
                fontSel = Fonts.u8g2_font_pixelle_micro_tr;
            case FONT_PIXELLE_MICRO_TN ->
                fontSel = Fonts.u8g2_font_pixelle_micro_tn;
            case FONT_ROBOT_DE_NIRO_TF ->
                fontSel = Fonts.u8g2_font_robot_de_niro_tf;
            case FONT_ROBOT_DE_NIRO_TR ->
                fontSel = Fonts.u8g2_font_robot_de_niro_tr;
            case FONT_ROBOT_DE_NIRO_TN ->
                fontSel = Fonts.u8g2_font_robot_de_niro_tn;
            case FONT_TRIXEL_SQUARE_TF ->
                fontSel = Fonts.u8g2_font_trixel_square_tf;
            case FONT_TRIXEL_SQUARE_TR ->
                fontSel = Fonts.u8g2_font_trixel_square_tr;
            case FONT_TRIXEL_SQUARE_TN ->
                fontSel = Fonts.u8g2_font_trixel_square_tn;
            case FONT_HAXRCORP4089_TR ->
                fontSel = Fonts.u8g2_font_haxrcorp4089_tr;
            case FONT_HAXRCORP4089_TN ->
                fontSel = Fonts.u8g2_font_haxrcorp4089_tn;
            case FONT_HAXRCORP4089_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_haxrcorp4089_t_cyrillic;
            case FONT_BUBBLE_TR ->
                fontSel = Fonts.u8g2_font_bubble_tr;
            case FONT_BUBBLE_TN ->
                fontSel = Fonts.u8g2_font_bubble_tn;
            case FONT_CARDIMON_PIXEL_TF ->
                fontSel = Fonts.u8g2_font_cardimon_pixel_tf;
            case FONT_CARDIMON_PIXEL_TR ->
                fontSel = Fonts.u8g2_font_cardimon_pixel_tr;
            case FONT_CARDIMON_PIXEL_TN ->
                fontSel = Fonts.u8g2_font_cardimon_pixel_tn;
            case FONT_MANIAC_TF ->
                fontSel = Fonts.u8g2_font_maniac_tf;
            case FONT_MANIAC_TR ->
                fontSel = Fonts.u8g2_font_maniac_tr;
            case FONT_MANIAC_TN ->
                fontSel = Fonts.u8g2_font_maniac_tn;
            case FONT_MANIAC_TE ->
                fontSel = Fonts.u8g2_font_maniac_te;
            case FONT_LUCASARTS_SCUMM_SUBTITLE_O_TF ->
                fontSel = Fonts.u8g2_font_lucasarts_scumm_subtitle_o_tf;
            case FONT_LUCASARTS_SCUMM_SUBTITLE_O_TR ->
                fontSel = Fonts.u8g2_font_lucasarts_scumm_subtitle_o_tr;
            case FONT_LUCASARTS_SCUMM_SUBTITLE_O_TN ->
                fontSel = Fonts.u8g2_font_lucasarts_scumm_subtitle_o_tn;
            case FONT_LUCASARTS_SCUMM_SUBTITLE_R_TF ->
                fontSel = Fonts.u8g2_font_lucasarts_scumm_subtitle_r_tf;
            case FONT_LUCASARTS_SCUMM_SUBTITLE_R_TR ->
                fontSel = Fonts.u8g2_font_lucasarts_scumm_subtitle_r_tr;
            case FONT_LUCASARTS_SCUMM_SUBTITLE_R_TN ->
                fontSel = Fonts.u8g2_font_lucasarts_scumm_subtitle_r_tn;
            case FONT_UTOPIA24_TF ->
                fontSel = Fonts.u8g2_font_utopia24_tf;
            case FONT_UTOPIA24_TR ->
                fontSel = Fonts.u8g2_font_utopia24_tr;
            case FONT_UTOPIA24_TN ->
                fontSel = Fonts.u8g2_font_utopia24_tn;
            case FONT_UTOPIA24_TE ->
                fontSel = Fonts.u8g2_font_utopia24_te;
            case FONT_M_C_KIDS_NES_CREDITS_FONT_TR ->
                fontSel = Fonts.u8g2_font_m_c_kids_nes_credits_font_tr;
            case FONT_CHARGEN_92_TF ->
                fontSel = Fonts.u8g2_font_chargen_92_tf;
            case FONT_CHARGEN_92_TR ->
                fontSel = Fonts.u8g2_font_chargen_92_tr;
            case FONT_CHARGEN_92_TN ->
                fontSel = Fonts.u8g2_font_chargen_92_tn;
            case FONT_CHARGEN_92_TE ->
                fontSel = Fonts.u8g2_font_chargen_92_te;
            case FONT_CHARGEN_92_MF ->
                fontSel = Fonts.u8g2_font_chargen_92_mf;
            case FONT_CHARGEN_92_MR ->
                fontSel = Fonts.u8g2_font_chargen_92_mr;
            case FONT_CHARGEN_92_MN ->
                fontSel = Fonts.u8g2_font_chargen_92_mn;
            case FONT_CHARGEN_92_ME ->
                fontSel = Fonts.u8g2_font_chargen_92_me;
            case FONT_FUB11_TF ->
                fontSel = Fonts.u8g2_font_fub11_tf;
            case FONT_FUB11_TR ->
                fontSel = Fonts.u8g2_font_fub11_tr;
            case FONT_FUB11_TN ->
                fontSel = Fonts.u8g2_font_fub11_tn;
            case FONT_FUB14_TF ->
                fontSel = Fonts.u8g2_font_fub14_tf;
            case FONT_FUB14_TR ->
                fontSel = Fonts.u8g2_font_fub14_tr;
            case FONT_FUB14_TN ->
                fontSel = Fonts.u8g2_font_fub14_tn;
            case FONT_FUB17_TF ->
                fontSel = Fonts.u8g2_font_fub17_tf;
            case FONT_FUB17_TR ->
                fontSel = Fonts.u8g2_font_fub17_tr;
            case FONT_FUB17_TN ->
                fontSel = Fonts.u8g2_font_fub17_tn;
            case FONT_FUB20_TF ->
                fontSel = Fonts.u8g2_font_fub20_tf;
            case FONT_FUB20_TR ->
                fontSel = Fonts.u8g2_font_fub20_tr;
            case FONT_FUB20_TN ->
                fontSel = Fonts.u8g2_font_fub20_tn;
            case FONT_FUB25_TF ->
                fontSel = Fonts.u8g2_font_fub25_tf;
            case FONT_FUB25_TR ->
                fontSel = Fonts.u8g2_font_fub25_tr;
            case FONT_FUB25_TN ->
                fontSel = Fonts.u8g2_font_fub25_tn;
            case FONT_FUB30_TF ->
                fontSel = Fonts.u8g2_font_fub30_tf;
            case FONT_FUB30_TR ->
                fontSel = Fonts.u8g2_font_fub30_tr;
            case FONT_FUB30_TN ->
                fontSel = Fonts.u8g2_font_fub30_tn;
            case FONT_FUB35_TF ->
                fontSel = Fonts.u8g2_font_fub35_tf;
            case FONT_FUB35_TR ->
                fontSel = Fonts.u8g2_font_fub35_tr;
            case FONT_FUB35_TN ->
                fontSel = Fonts.u8g2_font_fub35_tn;
            case FONT_FUB42_TF ->
                fontSel = Fonts.u8g2_font_fub42_tf;
            case FONT_FUB42_TR ->
                fontSel = Fonts.u8g2_font_fub42_tr;
            case FONT_FUB42_TN ->
                fontSel = Fonts.u8g2_font_fub42_tn;
            case FONT_FUB49_TN ->
                fontSel = Fonts.u8g2_font_fub49_tn;
            case FONT_FUB11_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_fub11_t_symbol;
            case FONT_FUB14_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_fub14_t_symbol;
            case FONT_FUB17_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_fub17_t_symbol;
            case FONT_FUB20_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_fub20_t_symbol;
            case FONT_FUB25_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_fub25_t_symbol;
            case FONT_FUB30_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_fub30_t_symbol;
            case FONT_FUB35_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_fub35_t_symbol;
            case FONT_FUB42_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_fub42_t_symbol;
            case FONT_FUB49_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_fub49_t_symbol;
            case FONT_FUR11_TF ->
                fontSel = Fonts.u8g2_font_fur11_tf;
            case FONT_FUR11_TR ->
                fontSel = Fonts.u8g2_font_fur11_tr;
            case FONT_FUR11_TN ->
                fontSel = Fonts.u8g2_font_fur11_tn;
            case FONT_FUR14_TF ->
                fontSel = Fonts.u8g2_font_fur14_tf;
            case FONT_FUR14_TR ->
                fontSel = Fonts.u8g2_font_fur14_tr;
            case FONT_FUR14_TN ->
                fontSel = Fonts.u8g2_font_fur14_tn;
            case FONT_FUR17_TF ->
                fontSel = Fonts.u8g2_font_fur17_tf;
            case FONT_FUR17_TR ->
                fontSel = Fonts.u8g2_font_fur17_tr;
            case FONT_FUR17_TN ->
                fontSel = Fonts.u8g2_font_fur17_tn;
            case FONT_FUR20_TF ->
                fontSel = Fonts.u8g2_font_fur20_tf;
            case FONT_FUR20_TR ->
                fontSel = Fonts.u8g2_font_fur20_tr;
            case FONT_FUR20_TN ->
                fontSel = Fonts.u8g2_font_fur20_tn;
            case FONT_FUR25_TF ->
                fontSel = Fonts.u8g2_font_fur25_tf;
            case FONT_FUR25_TR ->
                fontSel = Fonts.u8g2_font_fur25_tr;
            case FONT_FUR25_TN ->
                fontSel = Fonts.u8g2_font_fur25_tn;
            case FONT_FUR30_TF ->
                fontSel = Fonts.u8g2_font_fur30_tf;
            case FONT_FUR30_TR ->
                fontSel = Fonts.u8g2_font_fur30_tr;
            case FONT_FUR30_TN ->
                fontSel = Fonts.u8g2_font_fur30_tn;
            case FONT_FUR35_TF ->
                fontSel = Fonts.u8g2_font_fur35_tf;
            case FONT_FUR35_TR ->
                fontSel = Fonts.u8g2_font_fur35_tr;
            case FONT_FUR35_TN ->
                fontSel = Fonts.u8g2_font_fur35_tn;
            case FONT_FUR42_TF ->
                fontSel = Fonts.u8g2_font_fur42_tf;
            case FONT_FUR42_TR ->
                fontSel = Fonts.u8g2_font_fur42_tr;
            case FONT_FUR42_TN ->
                fontSel = Fonts.u8g2_font_fur42_tn;
            case FONT_FUR49_TN ->
                fontSel = Fonts.u8g2_font_fur49_tn;
            case FONT_FUR11_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_fur11_t_symbol;
            case FONT_FUR14_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_fur14_t_symbol;
            case FONT_FUR17_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_fur17_t_symbol;
            case FONT_FUR20_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_fur20_t_symbol;
            case FONT_FUR25_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_fur25_t_symbol;
            case FONT_FUR30_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_fur30_t_symbol;
            case FONT_FUR35_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_fur35_t_symbol;
            case FONT_FUR42_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_fur42_t_symbol;
            case FONT_FUR49_T_SYMBOL ->
                fontSel = Fonts.u8g2_font_fur49_t_symbol;
            case FONT_OSB18_TF ->
                fontSel = Fonts.u8g2_font_osb18_tf;
            case FONT_OSB18_TR ->
                fontSel = Fonts.u8g2_font_osb18_tr;
            case FONT_OSB18_TN ->
                fontSel = Fonts.u8g2_font_osb18_tn;
            case FONT_OSB21_TF ->
                fontSel = Fonts.u8g2_font_osb21_tf;
            case FONT_OSB21_TR ->
                fontSel = Fonts.u8g2_font_osb21_tr;
            case FONT_OSB21_TN ->
                fontSel = Fonts.u8g2_font_osb21_tn;
            case FONT_OSB26_TF ->
                fontSel = Fonts.u8g2_font_osb26_tf;
            case FONT_OSB26_TR ->
                fontSel = Fonts.u8g2_font_osb26_tr;
            case FONT_OSB26_TN ->
                fontSel = Fonts.u8g2_font_osb26_tn;
            case FONT_OSB29_TF ->
                fontSel = Fonts.u8g2_font_osb29_tf;
            case FONT_OSB29_TR ->
                fontSel = Fonts.u8g2_font_osb29_tr;
            case FONT_OSB29_TN ->
                fontSel = Fonts.u8g2_font_osb29_tn;
            case FONT_OSB35_TF ->
                fontSel = Fonts.u8g2_font_osb35_tf;
            case FONT_OSB35_TR ->
                fontSel = Fonts.u8g2_font_osb35_tr;
            case FONT_OSB35_TN ->
                fontSel = Fonts.u8g2_font_osb35_tn;
            case FONT_OSB41_TF ->
                fontSel = Fonts.u8g2_font_osb41_tf;
            case FONT_OSB41_TR ->
                fontSel = Fonts.u8g2_font_osb41_tr;
            case FONT_OSB41_TN ->
                fontSel = Fonts.u8g2_font_osb41_tn;
            case FONT_OSR18_TF ->
                fontSel = Fonts.u8g2_font_osr18_tf;
            case FONT_OSR18_TR ->
                fontSel = Fonts.u8g2_font_osr18_tr;
            case FONT_OSR18_TN ->
                fontSel = Fonts.u8g2_font_osr18_tn;
            case FONT_OSR21_TF ->
                fontSel = Fonts.u8g2_font_osr21_tf;
            case FONT_OSR21_TR ->
                fontSel = Fonts.u8g2_font_osr21_tr;
            case FONT_OSR21_TN ->
                fontSel = Fonts.u8g2_font_osr21_tn;
            case FONT_OSR26_TF ->
                fontSel = Fonts.u8g2_font_osr26_tf;
            case FONT_OSR26_TR ->
                fontSel = Fonts.u8g2_font_osr26_tr;
            case FONT_OSR26_TN ->
                fontSel = Fonts.u8g2_font_osr26_tn;
            case FONT_OSR29_TF ->
                fontSel = Fonts.u8g2_font_osr29_tf;
            case FONT_OSR29_TR ->
                fontSel = Fonts.u8g2_font_osr29_tr;
            case FONT_OSR29_TN ->
                fontSel = Fonts.u8g2_font_osr29_tn;
            case FONT_OSR35_TF ->
                fontSel = Fonts.u8g2_font_osr35_tf;
            case FONT_OSR35_TR ->
                fontSel = Fonts.u8g2_font_osr35_tr;
            case FONT_OSR35_TN ->
                fontSel = Fonts.u8g2_font_osr35_tn;
            case FONT_OSR41_TF ->
                fontSel = Fonts.u8g2_font_osr41_tf;
            case FONT_OSR41_TR ->
                fontSel = Fonts.u8g2_font_osr41_tr;
            case FONT_OSR41_TN ->
                fontSel = Fonts.u8g2_font_osr41_tn;
            case FONT_INR16_MF ->
                fontSel = Fonts.u8g2_font_inr16_mf;
            case FONT_INR16_MR ->
                fontSel = Fonts.u8g2_font_inr16_mr;
            case FONT_INR16_MN ->
                fontSel = Fonts.u8g2_font_inr16_mn;
            case FONT_INR19_MF ->
                fontSel = Fonts.u8g2_font_inr19_mf;
            case FONT_INR19_MR ->
                fontSel = Fonts.u8g2_font_inr19_mr;
            case FONT_INR19_MN ->
                fontSel = Fonts.u8g2_font_inr19_mn;
            case FONT_INR21_MF ->
                fontSel = Fonts.u8g2_font_inr21_mf;
            case FONT_INR21_MR ->
                fontSel = Fonts.u8g2_font_inr21_mr;
            case FONT_INR21_MN ->
                fontSel = Fonts.u8g2_font_inr21_mn;
            case FONT_INR24_MF ->
                fontSel = Fonts.u8g2_font_inr24_mf;
            case FONT_INR24_MR ->
                fontSel = Fonts.u8g2_font_inr24_mr;
            case FONT_INR24_MN ->
                fontSel = Fonts.u8g2_font_inr24_mn;
            case FONT_INR24_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_inr24_t_cyrillic;
            case FONT_INR27_MF ->
                fontSel = Fonts.u8g2_font_inr27_mf;
            case FONT_INR27_MR ->
                fontSel = Fonts.u8g2_font_inr27_mr;
            case FONT_INR27_MN ->
                fontSel = Fonts.u8g2_font_inr27_mn;
            case FONT_INR27_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_inr27_t_cyrillic;
            case FONT_INR30_MF ->
                fontSel = Fonts.u8g2_font_inr30_mf;
            case FONT_INR30_MR ->
                fontSel = Fonts.u8g2_font_inr30_mr;
            case FONT_INR30_MN ->
                fontSel = Fonts.u8g2_font_inr30_mn;
            case FONT_INR30_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_inr30_t_cyrillic;
            case FONT_INR33_MF ->
                fontSel = Fonts.u8g2_font_inr33_mf;
            case FONT_INR33_MR ->
                fontSel = Fonts.u8g2_font_inr33_mr;
            case FONT_INR33_MN ->
                fontSel = Fonts.u8g2_font_inr33_mn;
            case FONT_INR33_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_inr33_t_cyrillic;
            case FONT_INR38_MF ->
                fontSel = Fonts.u8g2_font_inr38_mf;
            case FONT_INR38_MR ->
                fontSel = Fonts.u8g2_font_inr38_mr;
            case FONT_INR38_MN ->
                fontSel = Fonts.u8g2_font_inr38_mn;
            case FONT_INR38_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_inr38_t_cyrillic;
            case FONT_INR42_MF ->
                fontSel = Fonts.u8g2_font_inr42_mf;
            case FONT_INR42_MR ->
                fontSel = Fonts.u8g2_font_inr42_mr;
            case FONT_INR42_MN ->
                fontSel = Fonts.u8g2_font_inr42_mn;
            case FONT_INR42_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_inr42_t_cyrillic;
            case FONT_INR46_MF ->
                fontSel = Fonts.u8g2_font_inr46_mf;
            case FONT_INR46_MR ->
                fontSel = Fonts.u8g2_font_inr46_mr;
            case FONT_INR46_MN ->
                fontSel = Fonts.u8g2_font_inr46_mn;
            case FONT_INR46_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_inr46_t_cyrillic;
            case FONT_INR49_MF ->
                fontSel = Fonts.u8g2_font_inr49_mf;
            case FONT_INR49_MR ->
                fontSel = Fonts.u8g2_font_inr49_mr;
            case FONT_INR49_MN ->
                fontSel = Fonts.u8g2_font_inr49_mn;
            case FONT_INR49_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_inr49_t_cyrillic;
            case FONT_INR53_MF ->
                fontSel = Fonts.u8g2_font_inr53_mf;
            case FONT_INR53_MR ->
                fontSel = Fonts.u8g2_font_inr53_mr;
            case FONT_INR53_MN ->
                fontSel = Fonts.u8g2_font_inr53_mn;
            case FONT_INR53_T_CYRILLIC ->
                fontSel = Fonts.u8g2_font_inr53_t_cyrillic;
            case FONT_INR57_MN ->
                fontSel = Fonts.u8g2_font_inr57_mn;
            case FONT_INR62_MN ->
                fontSel = Fonts.u8g2_font_inr62_mn;
            case FONT_INB16_MF ->
                fontSel = Fonts.u8g2_font_inb16_mf;
            case FONT_INB16_MR ->
                fontSel = Fonts.u8g2_font_inb16_mr;
            case FONT_INB16_MN ->
                fontSel = Fonts.u8g2_font_inb16_mn;
            case FONT_INB19_MF ->
                fontSel = Fonts.u8g2_font_inb19_mf;
            case FONT_INB19_MR ->
                fontSel = Fonts.u8g2_font_inb19_mr;
            case FONT_INB19_MN ->
                fontSel = Fonts.u8g2_font_inb19_mn;
            case FONT_INB21_MF ->
                fontSel = Fonts.u8g2_font_inb21_mf;
            case FONT_INB21_MR ->
                fontSel = Fonts.u8g2_font_inb21_mr;
            case FONT_INB21_MN ->
                fontSel = Fonts.u8g2_font_inb21_mn;
            case FONT_INB24_MF ->
                fontSel = Fonts.u8g2_font_inb24_mf;
            case FONT_INB24_MR ->
                fontSel = Fonts.u8g2_font_inb24_mr;
            case FONT_INB24_MN ->
                fontSel = Fonts.u8g2_font_inb24_mn;
            case FONT_INB27_MF ->
                fontSel = Fonts.u8g2_font_inb27_mf;
            case FONT_INB27_MR ->
                fontSel = Fonts.u8g2_font_inb27_mr;
            case FONT_INB27_MN ->
                fontSel = Fonts.u8g2_font_inb27_mn;
            case FONT_INB30_MF ->
                fontSel = Fonts.u8g2_font_inb30_mf;
            case FONT_INB30_MR ->
                fontSel = Fonts.u8g2_font_inb30_mr;
            case FONT_INB30_MN ->
                fontSel = Fonts.u8g2_font_inb30_mn;
            case FONT_INB33_MF ->
                fontSel = Fonts.u8g2_font_inb33_mf;
            case FONT_INB33_MR ->
                fontSel = Fonts.u8g2_font_inb33_mr;
            case FONT_INB33_MN ->
                fontSel = Fonts.u8g2_font_inb33_mn;
            case FONT_INB38_MF ->
                fontSel = Fonts.u8g2_font_inb38_mf;
            case FONT_INB38_MR ->
                fontSel = Fonts.u8g2_font_inb38_mr;
            case FONT_INB38_MN ->
                fontSel = Fonts.u8g2_font_inb38_mn;
            case FONT_INB42_MF ->
                fontSel = Fonts.u8g2_font_inb42_mf;
            case FONT_INB42_MR ->
                fontSel = Fonts.u8g2_font_inb42_mr;
            case FONT_INB42_MN ->
                fontSel = Fonts.u8g2_font_inb42_mn;
            case FONT_INB46_MF ->
                fontSel = Fonts.u8g2_font_inb46_mf;
            case FONT_INB46_MR ->
                fontSel = Fonts.u8g2_font_inb46_mr;
            case FONT_INB46_MN ->
                fontSel = Fonts.u8g2_font_inb46_mn;
            case FONT_INB49_MF ->
                fontSel = Fonts.u8g2_font_inb49_mf;
            case FONT_INB49_MR ->
                fontSel = Fonts.u8g2_font_inb49_mr;
            case FONT_INB49_MN ->
                fontSel = Fonts.u8g2_font_inb49_mn;
            case FONT_INB53_MF ->
                fontSel = Fonts.u8g2_font_inb53_mf;
            case FONT_INB53_MR ->
                fontSel = Fonts.u8g2_font_inb53_mr;
            case FONT_INB53_MN ->
                fontSel = Fonts.u8g2_font_inb53_mn;
            case FONT_INB57_MN ->
                fontSel = Fonts.u8g2_font_inb57_mn;
            case FONT_INB63_MN ->
                fontSel = Fonts.u8g2_font_inb63_mn;
            case FONT_LOGISOSO16_TF ->
                fontSel = Fonts.u8g2_font_logisoso16_tf;
            case FONT_LOGISOSO16_TR ->
                fontSel = Fonts.u8g2_font_logisoso16_tr;
            case FONT_LOGISOSO16_TN ->
                fontSel = Fonts.u8g2_font_logisoso16_tn;
            case FONT_LOGISOSO18_TF ->
                fontSel = Fonts.u8g2_font_logisoso18_tf;
            case FONT_LOGISOSO18_TR ->
                fontSel = Fonts.u8g2_font_logisoso18_tr;
            case FONT_LOGISOSO18_TN ->
                fontSel = Fonts.u8g2_font_logisoso18_tn;
            case FONT_LOGISOSO20_TF ->
                fontSel = Fonts.u8g2_font_logisoso20_tf;
            case FONT_LOGISOSO20_TR ->
                fontSel = Fonts.u8g2_font_logisoso20_tr;
            case FONT_LOGISOSO20_TN ->
                fontSel = Fonts.u8g2_font_logisoso20_tn;
            case FONT_LOGISOSO22_TF ->
                fontSel = Fonts.u8g2_font_logisoso22_tf;
            case FONT_LOGISOSO22_TR ->
                fontSel = Fonts.u8g2_font_logisoso22_tr;
            case FONT_LOGISOSO22_TN ->
                fontSel = Fonts.u8g2_font_logisoso22_tn;
            case FONT_LOGISOSO24_TF ->
                fontSel = Fonts.u8g2_font_logisoso24_tf;
            case FONT_LOGISOSO24_TR ->
                fontSel = Fonts.u8g2_font_logisoso24_tr;
            case FONT_LOGISOSO24_TN ->
                fontSel = Fonts.u8g2_font_logisoso24_tn;
            case FONT_LOGISOSO26_TF ->
                fontSel = Fonts.u8g2_font_logisoso26_tf;
            case FONT_LOGISOSO26_TR ->
                fontSel = Fonts.u8g2_font_logisoso26_tr;
            case FONT_LOGISOSO26_TN ->
                fontSel = Fonts.u8g2_font_logisoso26_tn;
            case FONT_LOGISOSO28_TF ->
                fontSel = Fonts.u8g2_font_logisoso28_tf;
            case FONT_LOGISOSO28_TR ->
                fontSel = Fonts.u8g2_font_logisoso28_tr;
            case FONT_LOGISOSO28_TN ->
                fontSel = Fonts.u8g2_font_logisoso28_tn;
            case FONT_LOGISOSO30_TF ->
                fontSel = Fonts.u8g2_font_logisoso30_tf;
            case FONT_LOGISOSO30_TR ->
                fontSel = Fonts.u8g2_font_logisoso30_tr;
            case FONT_LOGISOSO30_TN ->
                fontSel = Fonts.u8g2_font_logisoso30_tn;
            case FONT_LOGISOSO32_TF ->
                fontSel = Fonts.u8g2_font_logisoso32_tf;
            case FONT_LOGISOSO32_TR ->
                fontSel = Fonts.u8g2_font_logisoso32_tr;
            case FONT_LOGISOSO32_TN ->
                fontSel = Fonts.u8g2_font_logisoso32_tn;
            case FONT_LOGISOSO34_TF ->
                fontSel = Fonts.u8g2_font_logisoso34_tf;
            case FONT_LOGISOSO34_TR ->
                fontSel = Fonts.u8g2_font_logisoso34_tr;
            case FONT_LOGISOSO34_TN ->
                fontSel = Fonts.u8g2_font_logisoso34_tn;
            case FONT_LOGISOSO38_TF ->
                fontSel = Fonts.u8g2_font_logisoso38_tf;
            case FONT_LOGISOSO38_TR ->
                fontSel = Fonts.u8g2_font_logisoso38_tr;
            case FONT_LOGISOSO38_TN ->
                fontSel = Fonts.u8g2_font_logisoso38_tn;
            case FONT_LOGISOSO42_TF ->
                fontSel = Fonts.u8g2_font_logisoso42_tf;
            case FONT_LOGISOSO42_TR ->
                fontSel = Fonts.u8g2_font_logisoso42_tr;
            case FONT_LOGISOSO42_TN ->
                fontSel = Fonts.u8g2_font_logisoso42_tn;
            case FONT_LOGISOSO46_TF ->
                fontSel = Fonts.u8g2_font_logisoso46_tf;
            case FONT_LOGISOSO46_TR ->
                fontSel = Fonts.u8g2_font_logisoso46_tr;
            case FONT_LOGISOSO46_TN ->
                fontSel = Fonts.u8g2_font_logisoso46_tn;
            case FONT_LOGISOSO50_TF ->
                fontSel = Fonts.u8g2_font_logisoso50_tf;
            case FONT_LOGISOSO50_TR ->
                fontSel = Fonts.u8g2_font_logisoso50_tr;
            case FONT_LOGISOSO50_TN ->
                fontSel = Fonts.u8g2_font_logisoso50_tn;
            case FONT_LOGISOSO54_TF ->
                fontSel = Fonts.u8g2_font_logisoso54_tf;
            case FONT_LOGISOSO54_TR ->
                fontSel = Fonts.u8g2_font_logisoso54_tr;
            case FONT_LOGISOSO54_TN ->
                fontSel = Fonts.u8g2_font_logisoso54_tn;
            case FONT_LOGISOSO58_TF ->
                fontSel = Fonts.u8g2_font_logisoso58_tf;
            case FONT_LOGISOSO58_TR ->
                fontSel = Fonts.u8g2_font_logisoso58_tr;
            case FONT_LOGISOSO58_TN ->
                fontSel = Fonts.u8g2_font_logisoso58_tn;
            case FONT_LOGISOSO62_TN ->
                fontSel = Fonts.u8g2_font_logisoso62_tn;
            case FONT_LOGISOSO78_TN ->
                fontSel = Fonts.u8g2_font_logisoso78_tn;
            case FONT_LOGISOSO92_TN ->
                fontSel = Fonts.u8g2_font_logisoso92_tn;
            case FONT_GULIM11_T_KOREAN1 ->
                fontSel = Fonts.u8g2_font_gulim11_t_korean1;
            case FONT_GULIM11_T_KOREAN2 ->
                fontSel = Fonts.u8g2_font_gulim11_t_korean2;
            case FONT_GULIM12_T_KOREAN1 ->
                fontSel = Fonts.u8g2_font_gulim12_t_korean1;
            case FONT_GULIM12_T_KOREAN2 ->
                fontSel = Fonts.u8g2_font_gulim12_t_korean2;
            case FONT_GULIM14_T_KOREAN1 ->
                fontSel = Fonts.u8g2_font_gulim14_t_korean1;
            case FONT_GULIM14_T_KOREAN2 ->
                fontSel = Fonts.u8g2_font_gulim14_t_korean2;
            case FONT_GULIM16_T_KOREAN1 ->
                fontSel = Fonts.u8g2_font_gulim16_t_korean1;
            case FONT_GULIM16_T_KOREAN2 ->
                fontSel = Fonts.u8g2_font_gulim16_t_korean2;
            case FONT_PRESSSTART2P_8F ->
                fontSel = Fonts.u8g2_font_pressstart2p_8f;
            case FONT_PRESSSTART2P_8R ->
                fontSel = Fonts.u8g2_font_pressstart2p_8r;
            case FONT_PRESSSTART2P_8N ->
                fontSel = Fonts.u8g2_font_pressstart2p_8n;
            case FONT_PRESSSTART2P_8U ->
                fontSel = Fonts.u8g2_font_pressstart2p_8u;
            case FONT_PCSENIOR_8F ->
                fontSel = Fonts.u8g2_font_pcsenior_8f;
            case FONT_PCSENIOR_8R ->
                fontSel = Fonts.u8g2_font_pcsenior_8r;
            case FONT_PCSENIOR_8N ->
                fontSel = Fonts.u8g2_font_pcsenior_8n;
            case FONT_PCSENIOR_8U ->
                fontSel = Fonts.u8g2_font_pcsenior_8u;
            case FONT_PXPLUSIBMCGATHIN_8F ->
                fontSel = Fonts.u8g2_font_pxplusibmcgathin_8f;
            case FONT_PXPLUSIBMCGATHIN_8R ->
                fontSel = Fonts.u8g2_font_pxplusibmcgathin_8r;
            case FONT_PXPLUSIBMCGATHIN_8N ->
                fontSel = Fonts.u8g2_font_pxplusibmcgathin_8n;
            case FONT_PXPLUSIBMCGATHIN_8U ->
                fontSel = Fonts.u8g2_font_pxplusibmcgathin_8u;
            case FONT_PXPLUSIBMCGA_8F ->
                fontSel = Fonts.u8g2_font_pxplusibmcga_8f;
            case FONT_PXPLUSIBMCGA_8R ->
                fontSel = Fonts.u8g2_font_pxplusibmcga_8r;
            case FONT_PXPLUSIBMCGA_8N ->
                fontSel = Fonts.u8g2_font_pxplusibmcga_8n;
            case FONT_PXPLUSIBMCGA_8U ->
                fontSel = Fonts.u8g2_font_pxplusibmcga_8u;
            case FONT_PXPLUSTANDYNEWTV_8F ->
                fontSel = Fonts.u8g2_font_pxplustandynewtv_8f;
            case FONT_PXPLUSTANDYNEWTV_8R ->
                fontSel = Fonts.u8g2_font_pxplustandynewtv_8r;
            case FONT_PXPLUSTANDYNEWTV_8N ->
                fontSel = Fonts.u8g2_font_pxplustandynewtv_8n;
            case FONT_PXPLUSTANDYNEWTV_8U ->
                fontSel = Fonts.u8g2_font_pxplustandynewtv_8u;
            case FONT_PXPLUSTANDYNEWTV_T_ALL ->
                fontSel = Fonts.u8g2_font_pxplustandynewtv_t_all;
            case FONT_PXPLUSTANDYNEWTV_8_ALL ->
                fontSel = Fonts.u8g2_font_pxplustandynewtv_8_all;
            case FONT_PXPLUSIBMVGA9_TF ->
                fontSel = Fonts.u8g2_font_pxplusibmvga9_tf;
            case FONT_PXPLUSIBMVGA9_TR ->
                fontSel = Fonts.u8g2_font_pxplusibmvga9_tr;
            case FONT_PXPLUSIBMVGA9_TN ->
                fontSel = Fonts.u8g2_font_pxplusibmvga9_tn;
            case FONT_PXPLUSIBMVGA9_MF ->
                fontSel = Fonts.u8g2_font_pxplusibmvga9_mf;
            case FONT_PXPLUSIBMVGA9_MR ->
                fontSel = Fonts.u8g2_font_pxplusibmvga9_mr;
            case FONT_PXPLUSIBMVGA9_MN ->
                fontSel = Fonts.u8g2_font_pxplusibmvga9_mn;
            case FONT_PXPLUSIBMVGA9_T_ALL ->
                fontSel = Fonts.u8g2_font_pxplusibmvga9_t_all;
            case FONT_PXPLUSIBMVGA9_M_ALL ->
                fontSel = Fonts.u8g2_font_pxplusibmvga9_m_all;
            case FONT_PXPLUSIBMVGA8_TF ->
                fontSel = Fonts.u8g2_font_pxplusibmvga8_tf;
            case FONT_PXPLUSIBMVGA8_TR ->
                fontSel = Fonts.u8g2_font_pxplusibmvga8_tr;
            case FONT_PXPLUSIBMVGA8_TN ->
                fontSel = Fonts.u8g2_font_pxplusibmvga8_tn;
            case FONT_PXPLUSIBMVGA8_MF ->
                fontSel = Fonts.u8g2_font_pxplusibmvga8_mf;
            case FONT_PXPLUSIBMVGA8_MR ->
                fontSel = Fonts.u8g2_font_pxplusibmvga8_mr;
            case FONT_PXPLUSIBMVGA8_MN ->
                fontSel = Fonts.u8g2_font_pxplusibmvga8_mn;
            case FONT_PXPLUSIBMVGA8_T_ALL ->
                fontSel = Fonts.u8g2_font_pxplusibmvga8_t_all;
            case FONT_PXPLUSIBMVGA8_M_ALL ->
                fontSel = Fonts.u8g2_font_pxplusibmvga8_m_all;
            case FONT_PX437WYSE700A_TF ->
                fontSel = Fonts.u8g2_font_px437wyse700a_tf;
            case FONT_PX437WYSE700A_TR ->
                fontSel = Fonts.u8g2_font_px437wyse700a_tr;
            case FONT_PX437WYSE700A_TN ->
                fontSel = Fonts.u8g2_font_px437wyse700a_tn;
            case FONT_PX437WYSE700A_MF ->
                fontSel = Fonts.u8g2_font_px437wyse700a_mf;
            case FONT_PX437WYSE700A_MR ->
                fontSel = Fonts.u8g2_font_px437wyse700a_mr;
            case FONT_PX437WYSE700A_MN ->
                fontSel = Fonts.u8g2_font_px437wyse700a_mn;
            case FONT_PX437WYSE700B_TF ->
                fontSel = Fonts.u8g2_font_px437wyse700b_tf;
            case FONT_PX437WYSE700B_TR ->
                fontSel = Fonts.u8g2_font_px437wyse700b_tr;
            case FONT_PX437WYSE700B_TN ->
                fontSel = Fonts.u8g2_font_px437wyse700b_tn;
            case FONT_PX437WYSE700B_MF ->
                fontSel = Fonts.u8g2_font_px437wyse700b_mf;
            case FONT_PX437WYSE700B_MR ->
                fontSel = Fonts.u8g2_font_px437wyse700b_mr;
            case FONT_PX437WYSE700B_MN ->
                fontSel = Fonts.u8g2_font_px437wyse700b_mn;
        }
        return fontSel;
    }

    public void setupI2c(final SetupType setupType, final long u8g2, final long rotation, final long byteCb,
            final long gpioAndDelayCb) {
        switch (setupType) {
            case SSD1305_I2C_128X32_NONAME ->
                U8g2.setupSsd1305I2c128x32NonameF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1305_I2C_128X32_ADAFRUIT ->
                U8g2.setupSsd1305I2c128x32AdafruitF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1305_I2C_128X64_ADAFRUIT ->
                U8g2.setupSsd1305I2c128x64AdafruitF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1305_I2C_128X64_RAYSTAR ->
                U8g2.setupSsd1305I2c128x64RaystarF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1306_I2C_128X64_NONAME ->
                U8g2.setupSsd1306I2c128x64NonameF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1306_I2C_128X64_VCOMH0 ->
                U8g2.setupSsd1306I2c128x64Vcomh0F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1306_I2C_128X64_ALT0 ->
                U8g2.setupSsd1306I2c128x64Alt0F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1312_I2C_128X64_NONAME ->
                U8g2.setupSsd1312I2c128x64NonameF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1306_I2C_72X40_ER ->
                U8g2.setupSsd1306I2c72x40ErF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1306_I2C_96X40 ->
                U8g2.setupSsd1306I2c96x40F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1306_I2C_96X39 ->
                U8g2.setupSsd1306I2c96x39F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SH1106_I2C_128X64_NONAME ->
                U8g2.setupSh1106I2c128x64NonameF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SH1106_I2C_128X64_VCOMH0 ->
                U8g2.setupSh1106I2c128x64Vcomh0F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SH1106_I2C_128X64_WINSTAR ->
                U8g2.setupSh1106I2c128x64WinstarF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SH1106_I2C_72X40_WISE ->
                U8g2.setupSh1106I2c72x40WiseF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SH1106_I2C_64X32 ->
                U8g2.setupSh1106I2c64x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SH1107_I2C_64X128 ->
                U8g2.setupSh1107I2c64x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SH1107_I2C_SEEED_96X96 ->
                U8g2.setupSh1107I2cSeeed96x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SH1107_I2C_HJR_OEL1M0201_96X96 ->
                U8g2.setupSh1107I2cHjrOel1m020196x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SH1107_I2C_128X80 ->
                U8g2.setupSh1107I2c128x80F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SH1107_I2C_TK078F288_80X128 ->
                U8g2.setupSh1107I2cTk078f28880x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SH1107_I2C_128X128 ->
                U8g2.setupSh1107I2c128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SH1107_I2C_PIMORONI_128X128 ->
                U8g2.setupSh1107I2cPimoroni128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SH1107_I2C_SEEED_128X128 ->
                U8g2.setupSh1107I2cSeeed128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SH1108_I2C_128X160 ->
                U8g2.setupSh1108I2c128x160F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case CH1120_I2C_128X160 ->
                U8g2.setupCh1120I2c128x160F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SH1108_I2C_160X160 ->
                U8g2.setupSh1108I2c160x160F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SH1122_I2C_256X64 ->
                U8g2.setupSh1122I2c256x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1306_I2C_128X32_UNIVISION ->
                U8g2.setupSsd1306I2c128x32UnivisionF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1306_I2C_128X32_WINSTAR ->
                U8g2.setupSsd1306I2c128x32WinstarF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1306_I2C_102X64_EA_OLEDS102 ->
                U8g2.setupSsd1306I2c102x64EaOleds102F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SH1106_I2C_128X32_VISIONOX ->
                U8g2.setupSh1106I2c128x32VisionoxF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1306_I2C_64X48_ER ->
                U8g2.setupSsd1306I2c64x48ErF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1306_I2C_48X64_WINSTAR ->
                U8g2.setupSsd1306I2c48x64WinstarF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1306_I2C_64X32_NONAME ->
                U8g2.setupSsd1306I2c64x32NonameF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1306_I2C_64X32_1F ->
                U8g2.setupSsd1306I2c64x321fF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1306_I2C_96X16_ER ->
                U8g2.setupSsd1306I2c96x16ErF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1309_I2C_128X64_NONAME2 ->
                U8g2.setupSsd1309I2c128x64Noname2F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1309_I2C_128X64_NONAME0 ->
                U8g2.setupSsd1309I2c128x64Noname0F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1309_I2C_128X128_NONAME0 ->
                U8g2.setupSsd1309I2c128x128Noname0F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1312_I2C_128X32 ->
                U8g2.setupSsd1312I2c128x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1312_I2C_120X32 ->
                U8g2.setupSsd1312I2c120x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1312_I2C_120X28 ->
                U8g2.setupSsd1312I2c120x28F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1316_I2C_128X32 ->
                U8g2.setupSsd1316I2c128x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1316_I2C_96X32 ->
                U8g2.setupSsd1316I2c96x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1317_I2C_96X96 ->
                U8g2.setupSsd1317I2c96x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1318_I2C_128X96 ->
                U8g2.setupSsd1318I2c128x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1318_I2C_128X96_XCP ->
                U8g2.setupSsd1318I2c128x96XcpF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1325_I2C_NHD_128X64 ->
                U8g2.setupSsd1325I2cNhd128x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD0323_I2C_OS128064 ->
                U8g2.setupSsd0323I2cOs128064F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1326_I2C_ER_256X32 ->
                U8g2.setupSsd1326I2cEr256x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1327_I2C_WS_96X64 ->
                U8g2.setupSsd1327I2cWs96x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1327_I2C_SEEED_96X96 ->
                U8g2.setupSsd1327I2cSeeed96x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1327_I2C_EA_W128128 ->
                U8g2.setupSsd1327I2cEaW128128F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1327_I2C_MIDAS_128X128 ->
                U8g2.setupSsd1327I2cMidas128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1327_I2C_ZJY_128X128 ->
                U8g2.setupSsd1327I2cZjy128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1327_I2C_WS_128X128 ->
                U8g2.setupSsd1327I2cWs128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1327_I2C_VISIONOX_128X96 ->
                U8g2.setupSsd1327I2cVisionox128x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case LD7032_I2C_60X32 ->
                U8g2.setupLd7032I2c60x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case LD7032_I2C_60X32_ALT ->
                U8g2.setupLd7032I2c60x32AltF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case LD7032_I2C_128X36 ->
                U8g2.setupLd7032I2c128x36F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1604_I2C_JLX19264 ->
                U8g2.setupUc1604I2cJlx19264F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1604_I2C_JLX12864 ->
                U8g2.setupUc1604I2cJlx12864F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1608_I2C_ERC24064 ->
                U8g2.setupUc1608I2cErc24064F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1608_I2C_DEM240064 ->
                U8g2.setupUc1608I2cDem240064F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1608_I2C_ERC240120 ->
                U8g2.setupUc1608I2cErc240120F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1608_I2C_240X128 ->
                U8g2.setupUc1608I2c240x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1609_I2C_SLG19264 ->
                U8g2.setupUc1609I2cSlg19264F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1628_I2C_128X64 ->
                U8g2.setupUc1628I2c128x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1628_I2C_256X128 ->
                U8g2.setupUc1628I2c256x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1628_I2C_256X32 ->
                U8g2.setupUc1628I2c256x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1638_I2C_192X96 ->
                U8g2.setupUc1638I2c192x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1638_I2C_240X128 ->
                U8g2.setupUc1638I2c240x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1610_I2C_EA_DOGXL160 ->
                U8g2.setupUc1610I2cEaDogxl160F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1611_I2C_EA_DOGM240 ->
                U8g2.setupUc1611I2cEaDogm240F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1611_I2C_EA_DOGXL240 ->
                U8g2.setupUc1611I2cEaDogxl240F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1611_I2C_EW50850 ->
                U8g2.setupUc1611I2cEw50850F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1611_I2C_CG160160 ->
                U8g2.setupUc1611I2cCg160160F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1611_I2C_IDS4073 ->
                U8g2.setupUc1611I2cIds4073F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST7528_I2C_NHD_C160100 ->
                U8g2.setupSt7528I2cNhdC160100F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST7528_I2C_ERC16064 ->
                U8g2.setupSt7528I2cErc16064F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1617_I2C_JLX128128 ->
                U8g2.setupUc1617I2cJlx128128F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST7539_I2C_192X64 ->
                U8g2.setupSt7539I2c192x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1601_I2C_128X32 ->
                U8g2.setupUc1601I2c128x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case UC1601_I2C_128X64 ->
                U8g2.setupUc1601I2c128x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST7567_I2C_PI_132X64 ->
                U8g2.setupSt7567I2cPi132x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST7567_I2C_JLX12864 ->
                U8g2.setupSt7567I2cJlx12864F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST7567_I2C_ENH_DG128064 ->
                U8g2.setupSt7567I2cEnhDg128064F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST7567_I2C_ENH_DG128064I ->
                U8g2.setupSt7567I2cEnhDg128064iF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST7567_I2C_OS12864 ->
                U8g2.setupSt7567I2cOs12864F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST7567_I2C_ERC13232 ->
                U8g2.setupSt7567I2cErc13232F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST7567_I2C_96X65 ->
                U8g2.setupSt7567I2c96x65F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST7567_I2C_122X32 ->
                U8g2.setupSt7567I2c122x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST7567_I2C_64X32 ->
                U8g2.setupSt7567I2c64x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST7567_I2C_HEM6432 ->
                U8g2.setupSt7567I2cHem6432F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST7567_I2C_LW12832 ->
                U8g2.setupSt7567I2cLw12832F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST7571_I2C_128X128 ->
                U8g2.setupSt7571I2c128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST7571_I2C_128X96 ->
                U8g2.setupSt7571I2c128x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST7571_I2C_G12896 ->
                U8g2.setupSt7571I2cG12896F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST7588_I2C_JLX12864 ->
                U8g2.setupSt7588I2cJlx12864F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST75160_I2C_JM16096 ->
                U8g2.setupSt75160I2cJm16096F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST75161_I2C_JLX160160 ->
                U8g2.setupSt75161I2cJlx160160F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST75256_I2C_JLX256128 ->
                U8g2.setupSt75256I2cJlx256128F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST75256_I2C_WO256X128 ->
                U8g2.setupSt75256I2cWo256x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST75256_I2C_128X128 ->
                U8g2.setupSt75256I2c128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST75256_I2C_JLX256160 ->
                U8g2.setupSt75256I2cJlx256160F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST75256_I2C_JLX256160M ->
                U8g2.setupSt75256I2cJlx256160mF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST75256_I2C_JLX256160_ALT ->
                U8g2.setupSt75256I2cJlx256160AltF(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST75256_I2C_JLX240160 ->
                U8g2.setupSt75256I2cJlx240160F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST75256_I2C_JLX25664 ->
                U8g2.setupSt75256I2cJlx25664F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST75256_I2C_JLX172104 ->
                U8g2.setupSt75256I2cJlx172104F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST75256_I2C_JLX19296 ->
                U8g2.setupSt75256I2cJlx19296F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST75256_I2C_JLX16080 ->
                U8g2.setupSt75256I2cJlx16080F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case ST75320_I2C_JLX320240 ->
                U8g2.setupSt75320I2cJlx320240F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1320_I2C_160X80 ->
                U8g2.setupSsd1320I2c160x80F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1362_I2C_256X64 ->
                U8g2.setupSsd1362I2c256x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1362_I2C_206X36 ->
                U8g2.setupSsd1362I2c206x36F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1363_I2C_256X128 ->
                U8g2.setupSsd1363I2c256x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
            case SSD1315_I2C_128X64_NONAME ->
                U8g2.setupSsd1315I2c128x64NonameF(u8g2, rotation, byteCb, gpioAndDelayCb);
            default ->
                throw new RuntimeException(String.format("Setup type %s not supported for I2C", setupType));
        }
    }

    public void setupSpi(final SetupType setupType, final long u8g2, final long rotation, final long byteCb,
            final long gpioAndDelayCb) {
        switch (setupType) {

            case SSD1305_128X32_NONAME:
                U8g2.setupSsd1305128x32NonameF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1305_128X32_ADAFRUIT:
                U8g2.setupSsd1305128x32AdafruitF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1305_128X64_ADAFRUIT:
                U8g2.setupSsd1305128x64AdafruitF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1305_128X64_RAYSTAR:
                U8g2.setupSsd1305128x64RaystarF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_2040X16:
                U8g2.setupSsd13062040x16F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_128X64_NONAME:
                U8g2.setupSsd1306128x64NonameF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_128X64_VCOMH0:
                U8g2.setupSsd1306128x64Vcomh0F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_128X64_ALT0:
                U8g2.setupSsd1306128x64Alt0F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1312_128X64_NONAME:
                U8g2.setupSsd1312128x64NonameF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_72X40_ER:
                U8g2.setupSsd130672x40ErF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_96X40:
                U8g2.setupSsd130696x40F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_96X39:
                U8g2.setupSsd130696x39F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1106_128X64_NONAME:
                U8g2.setupSh1106128x64NonameF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1106_128X64_VCOMH0:
                U8g2.setupSh1106128x64Vcomh0F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1106_128X64_WINSTAR:
                U8g2.setupSh1106128x64WinstarF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1106_72X40_WISE:
                U8g2.setupSh110672x40WiseF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1106_64X32:
                U8g2.setupSh110664x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1107_64X128:
                U8g2.setupSh110764x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1107_SEEED_96X96:
                U8g2.setupSh1107Seeed96x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1107_HJR_OEL1M0201_96X96:
                U8g2.setupSh1107HjrOel1m020196x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1107_128X80:
                U8g2.setupSh1107128x80F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1107_TK078F288_80X128:
                U8g2.setupSh1107Tk078f28880x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1107_128X128:
                U8g2.setupSh1107128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1107_PIMORONI_128X128:
                U8g2.setupSh1107Pimoroni128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1107_SEEED_128X128:
                U8g2.setupSh1107Seeed128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1108_128X160:
                U8g2.setupSh1108128x160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case CH1120_128X160:
                U8g2.setupCh1120128x160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1108_160X160:
                U8g2.setupSh1108160x160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1122_256X64:
                U8g2.setupSh1122256x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_128X32_UNIVISION:
                U8g2.setupSsd1306128x32UnivisionF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_128X32_WINSTAR:
                U8g2.setupSsd1306128x32WinstarF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_102X64_EA_OLEDS102:
                U8g2.setupSsd1306102x64EaOleds102F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1106_128X32_VISIONOX:
                U8g2.setupSh1106128x32VisionoxF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_64X48_ER:
                U8g2.setupSsd130664x48ErF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_48X64_WINSTAR:
                U8g2.setupSsd130648x64WinstarF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_64X32_NONAME:
                U8g2.setupSsd130664x32NonameF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_64X32_1F:
                U8g2.setupSsd130664x321fF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_96X16_ER:
                U8g2.setupSsd130696x16ErF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1309_128X64_NONAME2:
                U8g2.setupSsd1309128x64Noname2F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1309_128X64_NONAME0:
                U8g2.setupSsd1309128x64Noname0F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1309_128X128_NONAME0:
                U8g2.setupSsd1309128x128Noname0F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1312_128X32:
                U8g2.setupSsd1312128x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1312_120X32:
                U8g2.setupSsd1312120x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1312_120X28:
                U8g2.setupSsd1312120x28F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1316_128X32:
                U8g2.setupSsd1316128x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1316_96X32:
                U8g2.setupSsd131696x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1317_96X96:
                U8g2.setupSsd131796x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1318_128X96:
                U8g2.setupSsd1318128x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1318_128X96_XCP:
                U8g2.setupSsd1318128x96XcpF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1325_NHD_128X64:
                U8g2.setupSsd1325Nhd128x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD0323_OS128064:
                U8g2.setupSsd0323Os128064F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1326_ER_256X32:
                U8g2.setupSsd1326Er256x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1327_WS_96X64:
                U8g2.setupSsd1327Ws96x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1327_SEEED_96X96:
                U8g2.setupSsd1327Seeed96x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1327_EA_W128128:
                U8g2.setupSsd1327EaW128128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1327_MIDAS_128X128:
                U8g2.setupSsd1327Midas128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1327_ZJY_128X128:
                U8g2.setupSsd1327Zjy128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1327_WS_128X128:
                U8g2.setupSsd1327Ws128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1327_VISIONOX_128X96:
                U8g2.setupSsd1327Visionox128x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1329_128X96_NONAME:
                U8g2.setupSsd1329128x96NonameF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1329_96X96_NONAME:
                U8g2.setupSsd132996x96NonameF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case LD7032_60X32:
                U8g2.setupLd703260x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case LD7032_60X32_ALT:
                U8g2.setupLd703260x32AltF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case LD7032_128X36:
                U8g2.setupLd7032128x36F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7920_P_256X32:
                U8g2.setupSt7920P256x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7920_256X32:
                U8g2.setupSt7920256x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7920_S_256X32:
                U8g2.setupSt7920S256x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7920_P_144X32:
                U8g2.setupSt7920P144x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7920_144X32:
                U8g2.setupSt7920144x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7920_S_144X32:
                U8g2.setupSt7920S144x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7920_P_128X32:
                U8g2.setupSt7920P128x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7920_128X32:
                U8g2.setupSt7920128x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7920_S_128X32:
                U8g2.setupSt7920S128x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7920_P_160X32:
                U8g2.setupSt7920P160x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7920_160X32:
                U8g2.setupSt7920160x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7920_S_160X32:
                U8g2.setupSt7920S160x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7920_P_192X32:
                U8g2.setupSt7920P192x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7920_192X32:
                U8g2.setupSt7920192x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7920_S_192X32:
                U8g2.setupSt7920S192x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7920_P_128X64:
                U8g2.setupSt7920P128x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7920_128X64:
                U8g2.setupSt7920128x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7920_S_128X64:
                U8g2.setupSt7920S128x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case LS013B7DH03_128X128:
                U8g2.setupLs013b7dh03128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case LS027B7DH01_400X240:
                U8g2.setupLs027b7dh01400x240F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case LS027B7DH01_M0_400X240:
                U8g2.setupLs027b7dh01M0400x240F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case LS013B7DH05_144X168:
                U8g2.setupLs013b7dh05144x168F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case LS011B7DH03_160X68:
                U8g2.setupLs011b7dh03160x68F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1701_EA_DOGS102:
                U8g2.setupUc1701EaDogs102F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1701_MINI12864:
                U8g2.setupUc1701Mini12864F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case PCD8544_84X48:
                U8g2.setupPcd854484x48F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case PCF8812_96X65:
                U8g2.setupPcf881296x65F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case PCF8812_101X64:
                U8g2.setupPcf8812101x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case HX1230_96X68:
                U8g2.setupHx123096x68F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1604_JLX19264:
                U8g2.setupUc1604Jlx19264F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1604_JLX12864:
                U8g2.setupUc1604Jlx12864F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1608_ERC24064:
                U8g2.setupUc1608Erc24064F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1608_DEM240064:
                U8g2.setupUc1608Dem240064F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1608_ERC240120:
                U8g2.setupUc1608Erc240120F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1608_240X128:
                U8g2.setupUc1608240x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1609_SLG19264:
                U8g2.setupUc1609Slg19264F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1628_128X64:
                U8g2.setupUc1628128x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1628_256X128:
                U8g2.setupUc1628256x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1628_256X32:
                U8g2.setupUc1628256x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1638_160X128:
                U8g2.setupUc1638160x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1638_192X96:
                U8g2.setupUc1638192x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1638_240X128:
                U8g2.setupUc1638240x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1610_EA_DOGXL160:
                U8g2.setupUc1610EaDogxl160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1611_EA_DOGM240:
                U8g2.setupUc1611EaDogm240F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1611_EA_DOGXL240:
                U8g2.setupUc1611EaDogxl240F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1611_EW50850:
                U8g2.setupUc1611Ew50850F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1611_CG160160:
                U8g2.setupUc1611Cg160160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1611_IDS4073:
                U8g2.setupUc1611Ids4073F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7511_AVD_320X240:
                U8g2.setupSt7511Avd320x240F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7528_NHD_C160100:
                U8g2.setupSt7528NhdC160100F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7528_ERC16064:
                U8g2.setupSt7528Erc16064F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1617_JLX128128:
                U8g2.setupUc1617Jlx128128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7565_EA_DOGM128:
                U8g2.setupSt7565EaDogm128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7565_LM6063:
                U8g2.setupSt7565Lm6063F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7565_64128N:
                U8g2.setupSt756564128nF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7565_ZOLEN_128X64:
                U8g2.setupSt7565Zolen128x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7565_LM6059:
                U8g2.setupSt7565Lm6059F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7565_KS0713:
                U8g2.setupSt7565Ks0713F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7565_LX12864:
                U8g2.setupSt7565Lx12864F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7565_ERC12864:
                U8g2.setupSt7565Erc12864F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7565_ERC12864_ALT:
                U8g2.setupSt7565Erc12864AltF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7565_NHD_C12864:
                U8g2.setupSt7565NhdC12864F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7565_JLX12864:
                U8g2.setupSt7565Jlx12864F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7565_NHD_C12832:
                U8g2.setupSt7565NhdC12832F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7539_192X64:
                U8g2.setupSt7539192x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1601_128X32:
                U8g2.setupUc1601128x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1601_128X64:
                U8g2.setupUc1601128x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7565_EA_DOGM132:
                U8g2.setupSt7565EaDogm132F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7567_PI_132X64:
                U8g2.setupSt7567Pi132x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7567_JLX12864:
                U8g2.setupSt7567Jlx12864F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7567_ENH_DG128064:
                U8g2.setupSt7567EnhDg128064F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7567_ENH_DG128064I:
                U8g2.setupSt7567EnhDg128064iF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7567_OS12864:
                U8g2.setupSt7567Os12864F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7567_ERC13232:
                U8g2.setupSt7567Erc13232F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7567_ERC12864:
                U8g2.setupSt7567Erc12864F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7567_96X65:
                U8g2.setupSt756796x65F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7567_122X32:
                U8g2.setupSt7567122x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7567_64X32:
                U8g2.setupSt756764x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7567_HEM6432:
                U8g2.setupSt7567Hem6432F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7567_LW12832:
                U8g2.setupSt7567Lw12832F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7567_YXD12832:
                U8g2.setupSt7567Yxd12832F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7571_128X128:
                U8g2.setupSt7571128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7571_128X96:
                U8g2.setupSt7571128x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7571_G12896:
                U8g2.setupSt7571G12896F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7302_122X250:
                U8g2.setupSt7302122x250F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7305_122X250:
                U8g2.setupSt7305122x250F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7305_200X200:
                U8g2.setupSt7305200x200F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7305_168X384:
                U8g2.setupSt7305168x384F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7586S_S028HN118A:
                U8g2.setupSt7586sS028hn118aF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7586S_JLX384160:
                U8g2.setupSt7586sJlx384160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7586S_ERC240160:
                U8g2.setupSt7586sErc240160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7586S_YMC240160:
                U8g2.setupSt7586sYmc240160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7586S_JLX320160:
                U8g2.setupSt7586sJlx320160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7586S_MD240128:
                U8g2.setupSt7586sMd240128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7588_JLX12864:
                U8g2.setupSt7588Jlx12864F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75160_JM16096:
                U8g2.setupSt75160Jm16096F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75161_JLX160160:
                U8g2.setupSt75161Jlx160160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_JLX256128:
                U8g2.setupSt75256Jlx256128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_WO256X128:
                U8g2.setupSt75256Wo256x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_128X128:
                U8g2.setupSt75256128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_JLX256160:
                U8g2.setupSt75256Jlx256160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_JLX256160M:
                U8g2.setupSt75256Jlx256160mF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_JLX256160_ALT:
                U8g2.setupSt75256Jlx256160AltF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_JLX240160:
                U8g2.setupSt75256Jlx240160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_JLX25664:
                U8g2.setupSt75256Jlx25664F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_JLX172104:
                U8g2.setupSt75256Jlx172104F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_JLX19296:
                U8g2.setupSt75256Jlx19296F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_JLX16080:
                U8g2.setupSt75256Jlx16080F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75320_JLX320240:
                U8g2.setupSt75320Jlx320240F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case NT7534_TG12864R:
                U8g2.setupNt7534Tg12864rF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case IST3020_ERC19264:
                U8g2.setupIst3020Erc19264F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case IST3088_320X240:
                U8g2.setupIst3088320x240F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case IST7920_128X128:
                U8g2.setupIst7920128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SBN1661_122X32:
                U8g2.setupSbn1661122x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SED1520_122X32:
                U8g2.setupSed1520122x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case KS0108_128X64:
                U8g2.setupKs0108128x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case KS0108_ERM19264:
                U8g2.setupKs0108Erm19264F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case T7932_150X32:
                U8g2.setupT7932150x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case HD44102_100X64:
                U8g2.setupHd44102100x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case LC7981_160X80:
                U8g2.setupLc7981160x80F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case LC7981_160X160:
                U8g2.setupLc7981160x160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case LC7981_240X128:
                U8g2.setupLc7981240x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case LC7981_240X64:
                U8g2.setupLc7981240x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case LC7981_128X128:
                U8g2.setupLc7981128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case T6963_240X128:
                U8g2.setupT6963240x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case T6963_240X64:
                U8g2.setupT6963240x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case T6963_256X64:
                U8g2.setupT6963256x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case T6963_128X64:
                U8g2.setupT6963128x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case T6963_128X64_ALT:
                U8g2.setupT6963128x64AltF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case T6963_160X80:
                U8g2.setupT6963160x80F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case T6963_128X128:
                U8g2.setupT6963128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case T6963_128X160:
                U8g2.setupT6963128x160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1320_160X32:
                U8g2.setupSsd1320160x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1320_128X72:
                U8g2.setupSsd1320128x72F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1320_160X132:
                U8g2.setupSsd1320160x132F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1320_160X80:
                U8g2.setupSsd1320160x80F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1322_240X128:
                U8g2.setupSsd1322240x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1322_TOPWIN_240X128:
                U8g2.setupSsd1322Topwin240x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1322_NHD_256X64:
                U8g2.setupSsd1322Nhd256x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1322_ZJY_256X64:
                U8g2.setupSsd1322Zjy256x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1322_NHD_128X64:
                U8g2.setupSsd1322Nhd128x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1362_256X64:
                U8g2.setupSsd1362256x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1362_206X36:
                U8g2.setupSsd1362206x36F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1363_256X128:
                U8g2.setupSsd1363256x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1606_172X72:
                U8g2.setupSsd1606172x72F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1607_200X200:
                U8g2.setupSsd1607200x200F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1607_GD_200X200:
                U8g2.setupSsd1607Gd200x200F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1607_WS_200X200:
                U8g2.setupSsd1607Ws200x200F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case IL3820_296X128:
                U8g2.setupIl3820296x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case IL3820_V2_296X128:
                U8g2.setupIl3820V2296x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SED1330_240X128:
                U8g2.setupSed1330240x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SED1330_240X64:
                U8g2.setupSed1330240x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SED1330_256X128:
                U8g2.setupSed1330256x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case RA8835_NHD_240X128:
                U8g2.setupRa8835Nhd240x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case RA8835_320X240:
                U8g2.setupRa8835320x240F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SED1330_320X200:
                U8g2.setupSed1330320x200F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case MAX7219_64X8:
                U8g2.setupMax721964x8F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case MAX7219_32X8:
                U8g2.setupMax721932x8F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case MAX7219_8X8:
                U8g2.setupMax72198x8F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case S1D15300_LM6023:
                U8g2.setupS1d15300Lm6023F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case S1D15300_97X32:
                U8g2.setupS1d1530097x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case S1D15300_100X32:
                U8g2.setupS1d15300100x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case S1D15300_100X32I:
                U8g2.setupS1d15300100x32iF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case S1D15E06_160100:
                U8g2.setupS1d15e06160100F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case S1D15721_240X64:
                U8g2.setupS1d15721240x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case GU800_128X64:
                U8g2.setupGu800128x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case GU800_160X16:
                U8g2.setupGu800160x16F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case GP1287AI_256X50:
                U8g2.setupGp1287ai256x50F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case GP1247AI_253X63:
                U8g2.setupGp1247ai253x63F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case GP1294AI_256X48:
                U8g2.setupGp1294ai256x48F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case A2PRINTER_384X240:
                U8g2.setupA2printer384x240F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1315_128X64_NONAME:
                U8g2.setupSsd1315128x64NonameF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            default:
                throw new RuntimeException(String.format("Setup type %s not supported for SPI", setupType));
        }
    }

    /**
     * Initialize I2C hardware driven display and return pointer to u8g2_t structure.
     *
     * @param setupType Setup type enum.
     * @param bus I2C bus number.
     * @param address I2C address.
     * @return Pointer to u8g2_t structure.
     */
    public long initHwI2c(final SetupType setupType, final int bus, final int address) {
        final var u8g2 = U8g2.initU8g2();
        setupI2c(setupType, u8g2, U8G2_R0, u8x8_byte_arm_linux_hw_i2c, u8x8_arm_linux_gpio_and_delay);
        U8g2.initI2cHw(u8g2, bus);
        U8g2.setI2CAddress(u8g2, address * 2);
        U8g2.initDisplay(u8g2);
        log.atDebug().log(String.format("Size %d x %d, draw color %d", U8g2.getDisplayWidth(u8g2), U8g2.getDisplayHeight(u8g2),
                U8g2.
                        getDrawColor(u8g2)));
        log.atDebug().log(String.format("Bus 0x%02x, Address %02x", bus, address));
        return u8g2;
    }

    /**
     * Initialize I2C software driven display and return pointer to u8g2_t structure.
     *
     * @param setupType Setup type enum.
     * @param gpio GPIO chip number.
     * @param scl SCL.
     * @param sda SDA.
     * @param res RESET pin.
     * @param delay Nanosecond delay or 0 for none.
     * @return Pointer to u8g2_t structure.
     */
    public long initSwI2c(final SetupType setupType, final int gpio, final int scl, final int sda, final int res, final long delay) {
        final var u8g2 = U8g2.initU8g2();
        setupI2c(setupType, u8g2, U8G2_R0, u8x8_byte_sw_i2c, u8x8_arm_linux_gpio_and_delay);
        U8g2.initI2cSw(u8g2, gpio, scl, sda, res, delay);
        U8g2.initDisplay(u8g2);
        log.atDebug().log(String.format("Size %d x %d, draw color %d", U8g2.getDisplayWidth(u8g2), U8g2.getDisplayHeight(u8g2),
                U8g2.
                        getDrawColor(u8g2)));
        log.atDebug().log(String.format("GPIO chip %d, SCL %d, SDA %d, RES %d, Delay %d", gpio, scl, sda, res, delay));
        return u8g2;
    }

    /**
     * Initialize SPI display and return pointer to u8g2_t structure.
     *
     * @param setupType Setup type enum.
     * @param gpio GPIO chip number.
     * @param bus SPI bus number.
     * @param dc DC pin.
     * @param res RESET pin.
     * @param cs CS pin.
     * @param spiMode SPI mode.
     * @param maxSpeed Maximum speed.
     * @return Pointer to u8g2_t structure.
     */
    public long initHwSpi(final SetupType setupType, final int gpio, final int bus, final int dc, final int res, final int cs,
            final short spiMode, final long maxSpeed) {
        final var u8g2 = U8g2.initU8g2();
        setupSpi(setupType, u8g2, U8G2_R0, u8x8_byte_arm_linux_hw_spi, u8x8_arm_linux_gpio_and_delay);
        U8g2.initSpiHwAdvanced(u8g2, gpio, bus, dc, res, cs, spiMode, maxSpeed);
        U8g2.initDisplay(u8g2);
        log.atDebug().log(String.format("Size %d x %d, draw color %d", U8g2.getDisplayWidth(u8g2), U8g2.getDisplayHeight(u8g2),
                U8g2.
                        getDrawColor(u8g2)));
        log.atDebug().log(String.format("GPIO chip %d, bus 0x%02x, DC %d, RES %d, CS %d", gpio, bus, dc, res, cs));
        return u8g2;
    }

    /**
     * Initialize SPI display and return pointer to u8g2_t structure.
     *
     * @param setupType Setup type enum.
     * @param gpio GPIO chip number.
     * @param dc DC pin.
     * @param res RESET pin.
     * @param mosi MOSI pin.
     * @param sck SCK pin.
     * @param cs CS pin.
     * @param delay Nanosecond delay or 0 for none.
     * @return Pointer to u8g2_t structure.
     */
    public long initSwSpi(final SetupType setupType, final int gpio, final int dc, final int res, final int mosi, final int sck,
            final int cs,
            final long delay) {
        final var u8g2 = U8g2.initU8g2();
        setupSpi(setupType, u8g2, U8G2_R0, u8x8_byte_4wire_sw_spi, u8x8_arm_linux_gpio_and_delay);
        U8g2.initSpiSw(u8g2, gpio, dc, res, mosi, sck, cs, delay);
        U8g2.initDisplay(u8g2);
        log.atDebug().log(String.format("Size %d x %d, draw color %d", U8g2.getDisplayWidth(u8g2), U8g2.getDisplayHeight(u8g2),
                U8g2.
                        getDrawColor(u8g2)));
        log.atDebug().log(String.format("GPIO chip %d, DC %d, RES %d, MOSI %d, SCK %d, CS %d, Delay %d", gpio, dc, res, mosi, sck,
                cs,
                delay));
        return u8g2;
    }

    /**
     * Initialize SDL display and return pointer to u8g2_t structure.
     *
     * @param setupType Setup type enum.
     * @return Pointer to u8g2_t structure.
     */
    public long initSdl(final SetupType setupType) {
        final var u8g2 = U8g2.initU8g2();
        switch (setupType) {
            case SSD1306_I2C_128X64_NONAME ->
                U8g2.setupbufferSdl128x64(u8g2, U8G2_R0);
            case SSD1363_256X128 ->
                U8g2.setupbufferSdl256x128(u8g2, U8G2_R0);
            default ->
                throw new RuntimeException(String.format("%s is not a valid setup type", setupType));
        }
        U8g2.initDisplay(u8g2);
        log.atDebug().log(String.format("Size %d x %d, draw color %d", U8g2.getDisplayWidth(u8g2), U8g2.getDisplayHeight(u8g2),
                U8g2.getDrawColor(u8g2)));
        return u8g2;
    }

    /**
     * Sleep for desired milliseconds.
     *
     * @param milliseconds Milliseconds to sleep.
     */
    public void sleep(final long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Free u8g2_t structure from memory and close down GPIO.
     *
     * @param u8g2 Pointer to u8g2_t structure.
     */
    public void done(final long u8g2) {
        log.atDebug().log("Done");
        U8g2.doneUserData(u8g2);
        U8g2.done(u8g2);
    }
}
