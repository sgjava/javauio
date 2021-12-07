/*
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 */
package com.codeferm.u8g2;

import static com.codeferm.u8g2.U8g2.U8G2_R0;
import static com.codeferm.u8g2.U8g2.u8x8_arm_linux_gpio_and_delay;
import static com.codeferm.u8g2.U8g2.u8x8_byte_4wire_sw_spi;
import static com.codeferm.u8g2.U8g2.u8x8_byte_arm_linux_hw_i2c;
import static com.codeferm.u8g2.U8g2.u8x8_byte_arm_linux_hw_spi;
import static com.codeferm.u8g2.U8g2.u8x8_byte_sw_i2c;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;

/**
 * Display allows dynamic selection of setup and fonts at run time. This is a monster class because of the generated switch
 * statements.
 *
 * @author Steven P. Goldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
public class Display {

    /**
     * Logger.
     */
    private final org.apache.logging.log4j.Logger logger = LogManager.getLogger(Display.class);

    /**
     * Return font pointer based on enum.
     *
     * @param fontType Font type eunm.
     * @return Font pointer.
     */
    public long getFontPtr(final FontType fontType) {
        long fontSel = 0;
        switch (fontType) {
            case FONT_U8GLIB_4_TF:
                fontSel = Fonts.u8g2_font_u8glib_4_tf;
                break;
            case FONT_U8GLIB_4_TR:
                fontSel = Fonts.u8g2_font_u8glib_4_tr;
                break;
            case FONT_U8GLIB_4_HF:
                fontSel = Fonts.u8g2_font_u8glib_4_hf;
                break;
            case FONT_U8GLIB_4_HR:
                fontSel = Fonts.u8g2_font_u8glib_4_hr;
                break;
            case FONT_M2ICON_5_TF:
                fontSel = Fonts.u8g2_font_m2icon_5_tf;
                break;
            case FONT_M2ICON_7_TF:
                fontSel = Fonts.u8g2_font_m2icon_7_tf;
                break;
            case FONT_M2ICON_9_TF:
                fontSel = Fonts.u8g2_font_m2icon_9_tf;
                break;
            case FONT_EMOTICONS21_TR:
                fontSel = Fonts.u8g2_font_emoticons21_tr;
                break;
            case FONT_BATTERY19_TN:
                fontSel = Fonts.u8g2_font_battery19_tn;
                break;
            case FONT_BATTERY24_TR:
                fontSel = Fonts.u8g2_font_battery24_tr;
                break;
            case FONT_SQUEEZED_R6_TR:
                fontSel = Fonts.u8g2_font_squeezed_r6_tr;
                break;
            case FONT_SQUEEZED_R6_TN:
                fontSel = Fonts.u8g2_font_squeezed_r6_tn;
                break;
            case FONT_SQUEEZED_B6_TR:
                fontSel = Fonts.u8g2_font_squeezed_b6_tr;
                break;
            case FONT_SQUEEZED_B6_TN:
                fontSel = Fonts.u8g2_font_squeezed_b6_tn;
                break;
            case FONT_SQUEEZED_R7_TR:
                fontSel = Fonts.u8g2_font_squeezed_r7_tr;
                break;
            case FONT_SQUEEZED_R7_TN:
                fontSel = Fonts.u8g2_font_squeezed_r7_tn;
                break;
            case FONT_SQUEEZED_B7_TR:
                fontSel = Fonts.u8g2_font_squeezed_b7_tr;
                break;
            case FONT_SQUEEZED_B7_TN:
                fontSel = Fonts.u8g2_font_squeezed_b7_tn;
                break;
            case FONT_FREEDOOMR10_TU:
                fontSel = Fonts.u8g2_font_freedoomr10_tu;
                break;
            case FONT_FREEDOOMR10_MU:
                fontSel = Fonts.u8g2_font_freedoomr10_mu;
                break;
            case FONT_FREEDOOMR25_TN:
                fontSel = Fonts.u8g2_font_freedoomr25_tn;
                break;
            case FONT_FREEDOOMR25_MN:
                fontSel = Fonts.u8g2_font_freedoomr25_mn;
                break;
            case FONT_7SEGMENTS_26X42_MN:
                fontSel = Fonts.u8g2_font_7Segments_26x42_mn;
                break;
            case FONT_AMSTRAD_CPC_EXTENDED_8F:
                fontSel = Fonts.u8g2_font_amstrad_cpc_extended_8f;
                break;
            case FONT_AMSTRAD_CPC_EXTENDED_8R:
                fontSel = Fonts.u8g2_font_amstrad_cpc_extended_8r;
                break;
            case FONT_AMSTRAD_CPC_EXTENDED_8N:
                fontSel = Fonts.u8g2_font_amstrad_cpc_extended_8n;
                break;
            case FONT_AMSTRAD_CPC_EXTENDED_8U:
                fontSel = Fonts.u8g2_font_amstrad_cpc_extended_8u;
                break;
            case FONT_CURSOR_TF:
                fontSel = Fonts.u8g2_font_cursor_tf;
                break;
            case FONT_CURSOR_TR:
                fontSel = Fonts.u8g2_font_cursor_tr;
                break;
            case FONT_MICRO_TR:
                fontSel = Fonts.u8g2_font_micro_tr;
                break;
            case FONT_MICRO_TN:
                fontSel = Fonts.u8g2_font_micro_tn;
                break;
            case FONT_MICRO_MR:
                fontSel = Fonts.u8g2_font_micro_mr;
                break;
            case FONT_MICRO_MN:
                fontSel = Fonts.u8g2_font_micro_mn;
                break;
            case FONT_4X6_TF:
                fontSel = Fonts.u8g2_font_4x6_tf;
                break;
            case FONT_4X6_TR:
                fontSel = Fonts.u8g2_font_4x6_tr;
                break;
            case FONT_4X6_TN:
                fontSel = Fonts.u8g2_font_4x6_tn;
                break;
            case FONT_4X6_MF:
                fontSel = Fonts.u8g2_font_4x6_mf;
                break;
            case FONT_4X6_MR:
                fontSel = Fonts.u8g2_font_4x6_mr;
                break;
            case FONT_4X6_MN:
                fontSel = Fonts.u8g2_font_4x6_mn;
                break;
            case FONT_4X6_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_4x6_t_cyrillic;
                break;
            case FONT_5X7_TF:
                fontSel = Fonts.u8g2_font_5x7_tf;
                break;
            case FONT_5X7_TR:
                fontSel = Fonts.u8g2_font_5x7_tr;
                break;
            case FONT_5X7_TN:
                fontSel = Fonts.u8g2_font_5x7_tn;
                break;
            case FONT_5X7_MF:
                fontSel = Fonts.u8g2_font_5x7_mf;
                break;
            case FONT_5X7_MR:
                fontSel = Fonts.u8g2_font_5x7_mr;
                break;
            case FONT_5X7_MN:
                fontSel = Fonts.u8g2_font_5x7_mn;
                break;
            case FONT_5X7_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_5x7_t_cyrillic;
                break;
            case FONT_5X8_TF:
                fontSel = Fonts.u8g2_font_5x8_tf;
                break;
            case FONT_5X8_TR:
                fontSel = Fonts.u8g2_font_5x8_tr;
                break;
            case FONT_5X8_TN:
                fontSel = Fonts.u8g2_font_5x8_tn;
                break;
            case FONT_5X8_MF:
                fontSel = Fonts.u8g2_font_5x8_mf;
                break;
            case FONT_5X8_MR:
                fontSel = Fonts.u8g2_font_5x8_mr;
                break;
            case FONT_5X8_MN:
                fontSel = Fonts.u8g2_font_5x8_mn;
                break;
            case FONT_5X8_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_5x8_t_cyrillic;
                break;
            case FONT_6X10_TF:
                fontSel = Fonts.u8g2_font_6x10_tf;
                break;
            case FONT_6X10_TR:
                fontSel = Fonts.u8g2_font_6x10_tr;
                break;
            case FONT_6X10_TN:
                fontSel = Fonts.u8g2_font_6x10_tn;
                break;
            case FONT_6X10_MF:
                fontSel = Fonts.u8g2_font_6x10_mf;
                break;
            case FONT_6X10_MR:
                fontSel = Fonts.u8g2_font_6x10_mr;
                break;
            case FONT_6X10_MN:
                fontSel = Fonts.u8g2_font_6x10_mn;
                break;
            case FONT_6X12_TF:
                fontSel = Fonts.u8g2_font_6x12_tf;
                break;
            case FONT_6X12_TR:
                fontSel = Fonts.u8g2_font_6x12_tr;
                break;
            case FONT_6X12_TN:
                fontSel = Fonts.u8g2_font_6x12_tn;
                break;
            case FONT_6X12_TE:
                fontSel = Fonts.u8g2_font_6x12_te;
                break;
            case FONT_6X12_MF:
                fontSel = Fonts.u8g2_font_6x12_mf;
                break;
            case FONT_6X12_MR:
                fontSel = Fonts.u8g2_font_6x12_mr;
                break;
            case FONT_6X12_MN:
                fontSel = Fonts.u8g2_font_6x12_mn;
                break;
            case FONT_6X12_ME:
                fontSel = Fonts.u8g2_font_6x12_me;
                break;
            case FONT_6X12_T_SYMBOLS:
                fontSel = Fonts.u8g2_font_6x12_t_symbols;
                break;
            case FONT_6X12_M_SYMBOLS:
                fontSel = Fonts.u8g2_font_6x12_m_symbols;
                break;
            case FONT_6X12_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_6x12_t_cyrillic;
                break;
            case FONT_6X13_TF:
                fontSel = Fonts.u8g2_font_6x13_tf;
                break;
            case FONT_6X13_TR:
                fontSel = Fonts.u8g2_font_6x13_tr;
                break;
            case FONT_6X13_TN:
                fontSel = Fonts.u8g2_font_6x13_tn;
                break;
            case FONT_6X13_TE:
                fontSel = Fonts.u8g2_font_6x13_te;
                break;
            case FONT_6X13_MF:
                fontSel = Fonts.u8g2_font_6x13_mf;
                break;
            case FONT_6X13_MR:
                fontSel = Fonts.u8g2_font_6x13_mr;
                break;
            case FONT_6X13_MN:
                fontSel = Fonts.u8g2_font_6x13_mn;
                break;
            case FONT_6X13_ME:
                fontSel = Fonts.u8g2_font_6x13_me;
                break;
            case FONT_6X13_T_HEBREW:
                fontSel = Fonts.u8g2_font_6x13_t_hebrew;
                break;
            case FONT_6X13_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_6x13_t_cyrillic;
                break;
            case FONT_6X13B_TF:
                fontSel = Fonts.u8g2_font_6x13B_tf;
                break;
            case FONT_6X13B_TR:
                fontSel = Fonts.u8g2_font_6x13B_tr;
                break;
            case FONT_6X13B_TN:
                fontSel = Fonts.u8g2_font_6x13B_tn;
                break;
            case FONT_6X13B_MF:
                fontSel = Fonts.u8g2_font_6x13B_mf;
                break;
            case FONT_6X13B_MR:
                fontSel = Fonts.u8g2_font_6x13B_mr;
                break;
            case FONT_6X13B_MN:
                fontSel = Fonts.u8g2_font_6x13B_mn;
                break;
            case FONT_6X13B_T_HEBREW:
                fontSel = Fonts.u8g2_font_6x13B_t_hebrew;
                break;
            case FONT_6X13B_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_6x13B_t_cyrillic;
                break;
            case FONT_6X13O_TF:
                fontSel = Fonts.u8g2_font_6x13O_tf;
                break;
            case FONT_6X13O_TR:
                fontSel = Fonts.u8g2_font_6x13O_tr;
                break;
            case FONT_6X13O_TN:
                fontSel = Fonts.u8g2_font_6x13O_tn;
                break;
            case FONT_6X13O_MF:
                fontSel = Fonts.u8g2_font_6x13O_mf;
                break;
            case FONT_6X13O_MR:
                fontSel = Fonts.u8g2_font_6x13O_mr;
                break;
            case FONT_6X13O_MN:
                fontSel = Fonts.u8g2_font_6x13O_mn;
                break;
            case FONT_7X13_TF:
                fontSel = Fonts.u8g2_font_7x13_tf;
                break;
            case FONT_7X13_TR:
                fontSel = Fonts.u8g2_font_7x13_tr;
                break;
            case FONT_7X13_TN:
                fontSel = Fonts.u8g2_font_7x13_tn;
                break;
            case FONT_7X13_TE:
                fontSel = Fonts.u8g2_font_7x13_te;
                break;
            case FONT_7X13_MF:
                fontSel = Fonts.u8g2_font_7x13_mf;
                break;
            case FONT_7X13_MR:
                fontSel = Fonts.u8g2_font_7x13_mr;
                break;
            case FONT_7X13_MN:
                fontSel = Fonts.u8g2_font_7x13_mn;
                break;
            case FONT_7X13_ME:
                fontSel = Fonts.u8g2_font_7x13_me;
                break;
            case FONT_7X13_T_SYMBOLS:
                fontSel = Fonts.u8g2_font_7x13_t_symbols;
                break;
            case FONT_7X13_M_SYMBOLS:
                fontSel = Fonts.u8g2_font_7x13_m_symbols;
                break;
            case FONT_7X13_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_7x13_t_cyrillic;
                break;
            case FONT_7X13B_TF:
                fontSel = Fonts.u8g2_font_7x13B_tf;
                break;
            case FONT_7X13B_TR:
                fontSel = Fonts.u8g2_font_7x13B_tr;
                break;
            case FONT_7X13B_TN:
                fontSel = Fonts.u8g2_font_7x13B_tn;
                break;
            case FONT_7X13B_MF:
                fontSel = Fonts.u8g2_font_7x13B_mf;
                break;
            case FONT_7X13B_MR:
                fontSel = Fonts.u8g2_font_7x13B_mr;
                break;
            case FONT_7X13B_MN:
                fontSel = Fonts.u8g2_font_7x13B_mn;
                break;
            case FONT_7X13O_TF:
                fontSel = Fonts.u8g2_font_7x13O_tf;
                break;
            case FONT_7X13O_TR:
                fontSel = Fonts.u8g2_font_7x13O_tr;
                break;
            case FONT_7X13O_TN:
                fontSel = Fonts.u8g2_font_7x13O_tn;
                break;
            case FONT_7X13O_MF:
                fontSel = Fonts.u8g2_font_7x13O_mf;
                break;
            case FONT_7X13O_MR:
                fontSel = Fonts.u8g2_font_7x13O_mr;
                break;
            case FONT_7X13O_MN:
                fontSel = Fonts.u8g2_font_7x13O_mn;
                break;
            case FONT_7X14_TF:
                fontSel = Fonts.u8g2_font_7x14_tf;
                break;
            case FONT_7X14_TR:
                fontSel = Fonts.u8g2_font_7x14_tr;
                break;
            case FONT_7X14_TN:
                fontSel = Fonts.u8g2_font_7x14_tn;
                break;
            case FONT_7X14_MF:
                fontSel = Fonts.u8g2_font_7x14_mf;
                break;
            case FONT_7X14_MR:
                fontSel = Fonts.u8g2_font_7x14_mr;
                break;
            case FONT_7X14_MN:
                fontSel = Fonts.u8g2_font_7x14_mn;
                break;
            case FONT_7X14B_TF:
                fontSel = Fonts.u8g2_font_7x14B_tf;
                break;
            case FONT_7X14B_TR:
                fontSel = Fonts.u8g2_font_7x14B_tr;
                break;
            case FONT_7X14B_TN:
                fontSel = Fonts.u8g2_font_7x14B_tn;
                break;
            case FONT_7X14B_MF:
                fontSel = Fonts.u8g2_font_7x14B_mf;
                break;
            case FONT_7X14B_MR:
                fontSel = Fonts.u8g2_font_7x14B_mr;
                break;
            case FONT_7X14B_MN:
                fontSel = Fonts.u8g2_font_7x14B_mn;
                break;
            case FONT_8X13_TF:
                fontSel = Fonts.u8g2_font_8x13_tf;
                break;
            case FONT_8X13_TR:
                fontSel = Fonts.u8g2_font_8x13_tr;
                break;
            case FONT_8X13_TN:
                fontSel = Fonts.u8g2_font_8x13_tn;
                break;
            case FONT_8X13_TE:
                fontSel = Fonts.u8g2_font_8x13_te;
                break;
            case FONT_8X13_MF:
                fontSel = Fonts.u8g2_font_8x13_mf;
                break;
            case FONT_8X13_MR:
                fontSel = Fonts.u8g2_font_8x13_mr;
                break;
            case FONT_8X13_MN:
                fontSel = Fonts.u8g2_font_8x13_mn;
                break;
            case FONT_8X13_ME:
                fontSel = Fonts.u8g2_font_8x13_me;
                break;
            case FONT_8X13_T_SYMBOLS:
                fontSel = Fonts.u8g2_font_8x13_t_symbols;
                break;
            case FONT_8X13_M_SYMBOLS:
                fontSel = Fonts.u8g2_font_8x13_m_symbols;
                break;
            case FONT_8X13_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_8x13_t_cyrillic;
                break;
            case FONT_8X13B_TF:
                fontSel = Fonts.u8g2_font_8x13B_tf;
                break;
            case FONT_8X13B_TR:
                fontSel = Fonts.u8g2_font_8x13B_tr;
                break;
            case FONT_8X13B_TN:
                fontSel = Fonts.u8g2_font_8x13B_tn;
                break;
            case FONT_8X13B_MF:
                fontSel = Fonts.u8g2_font_8x13B_mf;
                break;
            case FONT_8X13B_MR:
                fontSel = Fonts.u8g2_font_8x13B_mr;
                break;
            case FONT_8X13B_MN:
                fontSel = Fonts.u8g2_font_8x13B_mn;
                break;
            case FONT_8X13O_TF:
                fontSel = Fonts.u8g2_font_8x13O_tf;
                break;
            case FONT_8X13O_TR:
                fontSel = Fonts.u8g2_font_8x13O_tr;
                break;
            case FONT_8X13O_TN:
                fontSel = Fonts.u8g2_font_8x13O_tn;
                break;
            case FONT_8X13O_MF:
                fontSel = Fonts.u8g2_font_8x13O_mf;
                break;
            case FONT_8X13O_MR:
                fontSel = Fonts.u8g2_font_8x13O_mr;
                break;
            case FONT_8X13O_MN:
                fontSel = Fonts.u8g2_font_8x13O_mn;
                break;
            case FONT_9X15_TF:
                fontSel = Fonts.u8g2_font_9x15_tf;
                break;
            case FONT_9X15_TR:
                fontSel = Fonts.u8g2_font_9x15_tr;
                break;
            case FONT_9X15_TN:
                fontSel = Fonts.u8g2_font_9x15_tn;
                break;
            case FONT_9X15_TE:
                fontSel = Fonts.u8g2_font_9x15_te;
                break;
            case FONT_9X15_MF:
                fontSel = Fonts.u8g2_font_9x15_mf;
                break;
            case FONT_9X15_MR:
                fontSel = Fonts.u8g2_font_9x15_mr;
                break;
            case FONT_9X15_MN:
                fontSel = Fonts.u8g2_font_9x15_mn;
                break;
            case FONT_9X15_ME:
                fontSel = Fonts.u8g2_font_9x15_me;
                break;
            case FONT_9X15_T_SYMBOLS:
                fontSel = Fonts.u8g2_font_9x15_t_symbols;
                break;
            case FONT_9X15_M_SYMBOLS:
                fontSel = Fonts.u8g2_font_9x15_m_symbols;
                break;
            case FONT_9X15_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_9x15_t_cyrillic;
                break;
            case FONT_9X15B_TF:
                fontSel = Fonts.u8g2_font_9x15B_tf;
                break;
            case FONT_9X15B_TR:
                fontSel = Fonts.u8g2_font_9x15B_tr;
                break;
            case FONT_9X15B_TN:
                fontSel = Fonts.u8g2_font_9x15B_tn;
                break;
            case FONT_9X15B_MF:
                fontSel = Fonts.u8g2_font_9x15B_mf;
                break;
            case FONT_9X15B_MR:
                fontSel = Fonts.u8g2_font_9x15B_mr;
                break;
            case FONT_9X15B_MN:
                fontSel = Fonts.u8g2_font_9x15B_mn;
                break;
            case FONT_9X18_TF:
                fontSel = Fonts.u8g2_font_9x18_tf;
                break;
            case FONT_9X18_TR:
                fontSel = Fonts.u8g2_font_9x18_tr;
                break;
            case FONT_9X18_TN:
                fontSel = Fonts.u8g2_font_9x18_tn;
                break;
            case FONT_9X18_MF:
                fontSel = Fonts.u8g2_font_9x18_mf;
                break;
            case FONT_9X18_MR:
                fontSel = Fonts.u8g2_font_9x18_mr;
                break;
            case FONT_9X18_MN:
                fontSel = Fonts.u8g2_font_9x18_mn;
                break;
            case FONT_9X18B_TF:
                fontSel = Fonts.u8g2_font_9x18B_tf;
                break;
            case FONT_9X18B_TR:
                fontSel = Fonts.u8g2_font_9x18B_tr;
                break;
            case FONT_9X18B_TN:
                fontSel = Fonts.u8g2_font_9x18B_tn;
                break;
            case FONT_9X18B_MF:
                fontSel = Fonts.u8g2_font_9x18B_mf;
                break;
            case FONT_9X18B_MR:
                fontSel = Fonts.u8g2_font_9x18B_mr;
                break;
            case FONT_9X18B_MN:
                fontSel = Fonts.u8g2_font_9x18B_mn;
                break;
            case FONT_10X20_TF:
                fontSel = Fonts.u8g2_font_10x20_tf;
                break;
            case FONT_10X20_TR:
                fontSel = Fonts.u8g2_font_10x20_tr;
                break;
            case FONT_10X20_TN:
                fontSel = Fonts.u8g2_font_10x20_tn;
                break;
            case FONT_10X20_TE:
                fontSel = Fonts.u8g2_font_10x20_te;
                break;
            case FONT_10X20_MF:
                fontSel = Fonts.u8g2_font_10x20_mf;
                break;
            case FONT_10X20_MR:
                fontSel = Fonts.u8g2_font_10x20_mr;
                break;
            case FONT_10X20_MN:
                fontSel = Fonts.u8g2_font_10x20_mn;
                break;
            case FONT_10X20_ME:
                fontSel = Fonts.u8g2_font_10x20_me;
                break;
            case FONT_10X20_T_GREEK:
                fontSel = Fonts.u8g2_font_10x20_t_greek;
                break;
            case FONT_10X20_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_10x20_t_cyrillic;
                break;
            case FONT_10X20_T_ARABIC:
                fontSel = Fonts.u8g2_font_10x20_t_arabic;
                break;
            case FONT_SIJI_T_6X10:
                fontSel = Fonts.u8g2_font_siji_t_6x10;
                break;
            case FONT_TOM_THUMB_4X6_T_ALL:
                fontSel = Fonts.u8g2_font_tom_thumb_4x6_t_all;
                break;
            case FONT_TOM_THUMB_4X6_TF:
                fontSel = Fonts.u8g2_font_tom_thumb_4x6_tf;
                break;
            case FONT_TOM_THUMB_4X6_TR:
                fontSel = Fonts.u8g2_font_tom_thumb_4x6_tr;
                break;
            case FONT_TOM_THUMB_4X6_TN:
                fontSel = Fonts.u8g2_font_tom_thumb_4x6_tn;
                break;
            case FONT_TOM_THUMB_4X6_TE:
                fontSel = Fonts.u8g2_font_tom_thumb_4x6_te;
                break;
            case FONT_TOM_THUMB_4X6_MF:
                fontSel = Fonts.u8g2_font_tom_thumb_4x6_mf;
                break;
            case FONT_TOM_THUMB_4X6_MR:
                fontSel = Fonts.u8g2_font_tom_thumb_4x6_mr;
                break;
            case FONT_TOM_THUMB_4X6_MN:
                fontSel = Fonts.u8g2_font_tom_thumb_4x6_mn;
                break;
            case FONT_TOM_THUMB_4X6_ME:
                fontSel = Fonts.u8g2_font_tom_thumb_4x6_me;
                break;
            case FONT_T0_11_TF:
                fontSel = Fonts.u8g2_font_t0_11_tf;
                break;
            case FONT_T0_11_TR:
                fontSel = Fonts.u8g2_font_t0_11_tr;
                break;
            case FONT_T0_11_TN:
                fontSel = Fonts.u8g2_font_t0_11_tn;
                break;
            case FONT_T0_11_TE:
                fontSel = Fonts.u8g2_font_t0_11_te;
                break;
            case FONT_T0_11_MF:
                fontSel = Fonts.u8g2_font_t0_11_mf;
                break;
            case FONT_T0_11_MR:
                fontSel = Fonts.u8g2_font_t0_11_mr;
                break;
            case FONT_T0_11_MN:
                fontSel = Fonts.u8g2_font_t0_11_mn;
                break;
            case FONT_T0_11_ME:
                fontSel = Fonts.u8g2_font_t0_11_me;
                break;
            case FONT_T0_11_T_ALL:
                fontSel = Fonts.u8g2_font_t0_11_t_all;
                break;
            case FONT_T0_11B_TF:
                fontSel = Fonts.u8g2_font_t0_11b_tf;
                break;
            case FONT_T0_11B_TR:
                fontSel = Fonts.u8g2_font_t0_11b_tr;
                break;
            case FONT_T0_11B_TN:
                fontSel = Fonts.u8g2_font_t0_11b_tn;
                break;
            case FONT_T0_11B_TE:
                fontSel = Fonts.u8g2_font_t0_11b_te;
                break;
            case FONT_T0_11B_MF:
                fontSel = Fonts.u8g2_font_t0_11b_mf;
                break;
            case FONT_T0_11B_MR:
                fontSel = Fonts.u8g2_font_t0_11b_mr;
                break;
            case FONT_T0_11B_MN:
                fontSel = Fonts.u8g2_font_t0_11b_mn;
                break;
            case FONT_T0_11B_ME:
                fontSel = Fonts.u8g2_font_t0_11b_me;
                break;
            case FONT_T0_12_TF:
                fontSel = Fonts.u8g2_font_t0_12_tf;
                break;
            case FONT_T0_12_TR:
                fontSel = Fonts.u8g2_font_t0_12_tr;
                break;
            case FONT_T0_12_TN:
                fontSel = Fonts.u8g2_font_t0_12_tn;
                break;
            case FONT_T0_12_TE:
                fontSel = Fonts.u8g2_font_t0_12_te;
                break;
            case FONT_T0_12_MF:
                fontSel = Fonts.u8g2_font_t0_12_mf;
                break;
            case FONT_T0_12_MR:
                fontSel = Fonts.u8g2_font_t0_12_mr;
                break;
            case FONT_T0_12_MN:
                fontSel = Fonts.u8g2_font_t0_12_mn;
                break;
            case FONT_T0_12_ME:
                fontSel = Fonts.u8g2_font_t0_12_me;
                break;
            case FONT_T0_12B_TF:
                fontSel = Fonts.u8g2_font_t0_12b_tf;
                break;
            case FONT_T0_12B_TR:
                fontSel = Fonts.u8g2_font_t0_12b_tr;
                break;
            case FONT_T0_12B_TN:
                fontSel = Fonts.u8g2_font_t0_12b_tn;
                break;
            case FONT_T0_12B_TE:
                fontSel = Fonts.u8g2_font_t0_12b_te;
                break;
            case FONT_T0_12B_MF:
                fontSel = Fonts.u8g2_font_t0_12b_mf;
                break;
            case FONT_T0_12B_MR:
                fontSel = Fonts.u8g2_font_t0_12b_mr;
                break;
            case FONT_T0_12B_MN:
                fontSel = Fonts.u8g2_font_t0_12b_mn;
                break;
            case FONT_T0_12B_ME:
                fontSel = Fonts.u8g2_font_t0_12b_me;
                break;
            case FONT_T0_13_TF:
                fontSel = Fonts.u8g2_font_t0_13_tf;
                break;
            case FONT_T0_13_TR:
                fontSel = Fonts.u8g2_font_t0_13_tr;
                break;
            case FONT_T0_13_TN:
                fontSel = Fonts.u8g2_font_t0_13_tn;
                break;
            case FONT_T0_13_TE:
                fontSel = Fonts.u8g2_font_t0_13_te;
                break;
            case FONT_T0_13_MF:
                fontSel = Fonts.u8g2_font_t0_13_mf;
                break;
            case FONT_T0_13_MR:
                fontSel = Fonts.u8g2_font_t0_13_mr;
                break;
            case FONT_T0_13_MN:
                fontSel = Fonts.u8g2_font_t0_13_mn;
                break;
            case FONT_T0_13_ME:
                fontSel = Fonts.u8g2_font_t0_13_me;
                break;
            case FONT_T0_13B_TF:
                fontSel = Fonts.u8g2_font_t0_13b_tf;
                break;
            case FONT_T0_13B_TR:
                fontSel = Fonts.u8g2_font_t0_13b_tr;
                break;
            case FONT_T0_13B_TN:
                fontSel = Fonts.u8g2_font_t0_13b_tn;
                break;
            case FONT_T0_13B_TE:
                fontSel = Fonts.u8g2_font_t0_13b_te;
                break;
            case FONT_T0_13B_MF:
                fontSel = Fonts.u8g2_font_t0_13b_mf;
                break;
            case FONT_T0_13B_MR:
                fontSel = Fonts.u8g2_font_t0_13b_mr;
                break;
            case FONT_T0_13B_MN:
                fontSel = Fonts.u8g2_font_t0_13b_mn;
                break;
            case FONT_T0_13B_ME:
                fontSel = Fonts.u8g2_font_t0_13b_me;
                break;
            case FONT_T0_14_TF:
                fontSel = Fonts.u8g2_font_t0_14_tf;
                break;
            case FONT_T0_14_TR:
                fontSel = Fonts.u8g2_font_t0_14_tr;
                break;
            case FONT_T0_14_TN:
                fontSel = Fonts.u8g2_font_t0_14_tn;
                break;
            case FONT_T0_14_TE:
                fontSel = Fonts.u8g2_font_t0_14_te;
                break;
            case FONT_T0_14_MF:
                fontSel = Fonts.u8g2_font_t0_14_mf;
                break;
            case FONT_T0_14_MR:
                fontSel = Fonts.u8g2_font_t0_14_mr;
                break;
            case FONT_T0_14_MN:
                fontSel = Fonts.u8g2_font_t0_14_mn;
                break;
            case FONT_T0_14_ME:
                fontSel = Fonts.u8g2_font_t0_14_me;
                break;
            case FONT_T0_14B_TF:
                fontSel = Fonts.u8g2_font_t0_14b_tf;
                break;
            case FONT_T0_14B_TR:
                fontSel = Fonts.u8g2_font_t0_14b_tr;
                break;
            case FONT_T0_14B_TN:
                fontSel = Fonts.u8g2_font_t0_14b_tn;
                break;
            case FONT_T0_14B_TE:
                fontSel = Fonts.u8g2_font_t0_14b_te;
                break;
            case FONT_T0_14B_MF:
                fontSel = Fonts.u8g2_font_t0_14b_mf;
                break;
            case FONT_T0_14B_MR:
                fontSel = Fonts.u8g2_font_t0_14b_mr;
                break;
            case FONT_T0_14B_MN:
                fontSel = Fonts.u8g2_font_t0_14b_mn;
                break;
            case FONT_T0_14B_ME:
                fontSel = Fonts.u8g2_font_t0_14b_me;
                break;
            case FONT_T0_15_TF:
                fontSel = Fonts.u8g2_font_t0_15_tf;
                break;
            case FONT_T0_15_TR:
                fontSel = Fonts.u8g2_font_t0_15_tr;
                break;
            case FONT_T0_15_TN:
                fontSel = Fonts.u8g2_font_t0_15_tn;
                break;
            case FONT_T0_15_TE:
                fontSel = Fonts.u8g2_font_t0_15_te;
                break;
            case FONT_T0_15_MF:
                fontSel = Fonts.u8g2_font_t0_15_mf;
                break;
            case FONT_T0_15_MR:
                fontSel = Fonts.u8g2_font_t0_15_mr;
                break;
            case FONT_T0_15_MN:
                fontSel = Fonts.u8g2_font_t0_15_mn;
                break;
            case FONT_T0_15_ME:
                fontSel = Fonts.u8g2_font_t0_15_me;
                break;
            case FONT_T0_15B_TF:
                fontSel = Fonts.u8g2_font_t0_15b_tf;
                break;
            case FONT_T0_15B_TR:
                fontSel = Fonts.u8g2_font_t0_15b_tr;
                break;
            case FONT_T0_15B_TN:
                fontSel = Fonts.u8g2_font_t0_15b_tn;
                break;
            case FONT_T0_15B_TE:
                fontSel = Fonts.u8g2_font_t0_15b_te;
                break;
            case FONT_T0_15B_MF:
                fontSel = Fonts.u8g2_font_t0_15b_mf;
                break;
            case FONT_T0_15B_MR:
                fontSel = Fonts.u8g2_font_t0_15b_mr;
                break;
            case FONT_T0_15B_MN:
                fontSel = Fonts.u8g2_font_t0_15b_mn;
                break;
            case FONT_T0_15B_ME:
                fontSel = Fonts.u8g2_font_t0_15b_me;
                break;
            case FONT_T0_16_TF:
                fontSel = Fonts.u8g2_font_t0_16_tf;
                break;
            case FONT_T0_16_TR:
                fontSel = Fonts.u8g2_font_t0_16_tr;
                break;
            case FONT_T0_16_TN:
                fontSel = Fonts.u8g2_font_t0_16_tn;
                break;
            case FONT_T0_16_TE:
                fontSel = Fonts.u8g2_font_t0_16_te;
                break;
            case FONT_T0_16_MF:
                fontSel = Fonts.u8g2_font_t0_16_mf;
                break;
            case FONT_T0_16_MR:
                fontSel = Fonts.u8g2_font_t0_16_mr;
                break;
            case FONT_T0_16_MN:
                fontSel = Fonts.u8g2_font_t0_16_mn;
                break;
            case FONT_T0_16_ME:
                fontSel = Fonts.u8g2_font_t0_16_me;
                break;
            case FONT_T0_16B_TF:
                fontSel = Fonts.u8g2_font_t0_16b_tf;
                break;
            case FONT_T0_16B_TR:
                fontSel = Fonts.u8g2_font_t0_16b_tr;
                break;
            case FONT_T0_16B_TN:
                fontSel = Fonts.u8g2_font_t0_16b_tn;
                break;
            case FONT_T0_16B_TE:
                fontSel = Fonts.u8g2_font_t0_16b_te;
                break;
            case FONT_T0_16B_MF:
                fontSel = Fonts.u8g2_font_t0_16b_mf;
                break;
            case FONT_T0_16B_MR:
                fontSel = Fonts.u8g2_font_t0_16b_mr;
                break;
            case FONT_T0_16B_MN:
                fontSel = Fonts.u8g2_font_t0_16b_mn;
                break;
            case FONT_T0_16B_ME:
                fontSel = Fonts.u8g2_font_t0_16b_me;
                break;
            case FONT_T0_17_TF:
                fontSel = Fonts.u8g2_font_t0_17_tf;
                break;
            case FONT_T0_17_TR:
                fontSel = Fonts.u8g2_font_t0_17_tr;
                break;
            case FONT_T0_17_TN:
                fontSel = Fonts.u8g2_font_t0_17_tn;
                break;
            case FONT_T0_17_TE:
                fontSel = Fonts.u8g2_font_t0_17_te;
                break;
            case FONT_T0_17_MF:
                fontSel = Fonts.u8g2_font_t0_17_mf;
                break;
            case FONT_T0_17_MR:
                fontSel = Fonts.u8g2_font_t0_17_mr;
                break;
            case FONT_T0_17_MN:
                fontSel = Fonts.u8g2_font_t0_17_mn;
                break;
            case FONT_T0_17_ME:
                fontSel = Fonts.u8g2_font_t0_17_me;
                break;
            case FONT_T0_17B_TF:
                fontSel = Fonts.u8g2_font_t0_17b_tf;
                break;
            case FONT_T0_17B_TR:
                fontSel = Fonts.u8g2_font_t0_17b_tr;
                break;
            case FONT_T0_17B_TN:
                fontSel = Fonts.u8g2_font_t0_17b_tn;
                break;
            case FONT_T0_17B_TE:
                fontSel = Fonts.u8g2_font_t0_17b_te;
                break;
            case FONT_T0_17B_MF:
                fontSel = Fonts.u8g2_font_t0_17b_mf;
                break;
            case FONT_T0_17B_MR:
                fontSel = Fonts.u8g2_font_t0_17b_mr;
                break;
            case FONT_T0_17B_MN:
                fontSel = Fonts.u8g2_font_t0_17b_mn;
                break;
            case FONT_T0_17B_ME:
                fontSel = Fonts.u8g2_font_t0_17b_me;
                break;
            case FONT_T0_18_TF:
                fontSel = Fonts.u8g2_font_t0_18_tf;
                break;
            case FONT_T0_18_TR:
                fontSel = Fonts.u8g2_font_t0_18_tr;
                break;
            case FONT_T0_18_TN:
                fontSel = Fonts.u8g2_font_t0_18_tn;
                break;
            case FONT_T0_18_TE:
                fontSel = Fonts.u8g2_font_t0_18_te;
                break;
            case FONT_T0_18_MF:
                fontSel = Fonts.u8g2_font_t0_18_mf;
                break;
            case FONT_T0_18_MR:
                fontSel = Fonts.u8g2_font_t0_18_mr;
                break;
            case FONT_T0_18_MN:
                fontSel = Fonts.u8g2_font_t0_18_mn;
                break;
            case FONT_T0_18_ME:
                fontSel = Fonts.u8g2_font_t0_18_me;
                break;
            case FONT_T0_18B_TF:
                fontSel = Fonts.u8g2_font_t0_18b_tf;
                break;
            case FONT_T0_18B_TR:
                fontSel = Fonts.u8g2_font_t0_18b_tr;
                break;
            case FONT_T0_18B_TN:
                fontSel = Fonts.u8g2_font_t0_18b_tn;
                break;
            case FONT_T0_18B_TE:
                fontSel = Fonts.u8g2_font_t0_18b_te;
                break;
            case FONT_T0_18B_MF:
                fontSel = Fonts.u8g2_font_t0_18b_mf;
                break;
            case FONT_T0_18B_MR:
                fontSel = Fonts.u8g2_font_t0_18b_mr;
                break;
            case FONT_T0_18B_MN:
                fontSel = Fonts.u8g2_font_t0_18b_mn;
                break;
            case FONT_T0_18B_ME:
                fontSel = Fonts.u8g2_font_t0_18b_me;
                break;
            case FONT_T0_22_TF:
                fontSel = Fonts.u8g2_font_t0_22_tf;
                break;
            case FONT_T0_22_TR:
                fontSel = Fonts.u8g2_font_t0_22_tr;
                break;
            case FONT_T0_22_TN:
                fontSel = Fonts.u8g2_font_t0_22_tn;
                break;
            case FONT_T0_22_TE:
                fontSel = Fonts.u8g2_font_t0_22_te;
                break;
            case FONT_T0_22_MF:
                fontSel = Fonts.u8g2_font_t0_22_mf;
                break;
            case FONT_T0_22_MR:
                fontSel = Fonts.u8g2_font_t0_22_mr;
                break;
            case FONT_T0_22_MN:
                fontSel = Fonts.u8g2_font_t0_22_mn;
                break;
            case FONT_T0_22_ME:
                fontSel = Fonts.u8g2_font_t0_22_me;
                break;
            case FONT_T0_22B_TF:
                fontSel = Fonts.u8g2_font_t0_22b_tf;
                break;
            case FONT_T0_22B_TR:
                fontSel = Fonts.u8g2_font_t0_22b_tr;
                break;
            case FONT_T0_22B_TN:
                fontSel = Fonts.u8g2_font_t0_22b_tn;
                break;
            case FONT_T0_22B_TE:
                fontSel = Fonts.u8g2_font_t0_22b_te;
                break;
            case FONT_T0_22B_MF:
                fontSel = Fonts.u8g2_font_t0_22b_mf;
                break;
            case FONT_T0_22B_MR:
                fontSel = Fonts.u8g2_font_t0_22b_mr;
                break;
            case FONT_T0_22B_MN:
                fontSel = Fonts.u8g2_font_t0_22b_mn;
                break;
            case FONT_T0_22B_ME:
                fontSel = Fonts.u8g2_font_t0_22b_me;
                break;
            case FONT_OPEN_ICONIC_ALL_1X_T:
                fontSel = Fonts.u8g2_font_open_iconic_all_1x_t;
                break;
            case FONT_OPEN_ICONIC_APP_1X_T:
                fontSel = Fonts.u8g2_font_open_iconic_app_1x_t;
                break;
            case FONT_OPEN_ICONIC_ARROW_1X_T:
                fontSel = Fonts.u8g2_font_open_iconic_arrow_1x_t;
                break;
            case FONT_OPEN_ICONIC_CHECK_1X_T:
                fontSel = Fonts.u8g2_font_open_iconic_check_1x_t;
                break;
            case FONT_OPEN_ICONIC_EMAIL_1X_T:
                fontSel = Fonts.u8g2_font_open_iconic_email_1x_t;
                break;
            case FONT_OPEN_ICONIC_EMBEDDED_1X_T:
                fontSel = Fonts.u8g2_font_open_iconic_embedded_1x_t;
                break;
            case FONT_OPEN_ICONIC_GUI_1X_T:
                fontSel = Fonts.u8g2_font_open_iconic_gui_1x_t;
                break;
            case FONT_OPEN_ICONIC_HUMAN_1X_T:
                fontSel = Fonts.u8g2_font_open_iconic_human_1x_t;
                break;
            case FONT_OPEN_ICONIC_MIME_1X_T:
                fontSel = Fonts.u8g2_font_open_iconic_mime_1x_t;
                break;
            case FONT_OPEN_ICONIC_OTHER_1X_T:
                fontSel = Fonts.u8g2_font_open_iconic_other_1x_t;
                break;
            case FONT_OPEN_ICONIC_PLAY_1X_T:
                fontSel = Fonts.u8g2_font_open_iconic_play_1x_t;
                break;
            case FONT_OPEN_ICONIC_TEXT_1X_T:
                fontSel = Fonts.u8g2_font_open_iconic_text_1x_t;
                break;
            case FONT_OPEN_ICONIC_THING_1X_T:
                fontSel = Fonts.u8g2_font_open_iconic_thing_1x_t;
                break;
            case FONT_OPEN_ICONIC_WEATHER_1X_T:
                fontSel = Fonts.u8g2_font_open_iconic_weather_1x_t;
                break;
            case FONT_OPEN_ICONIC_WWW_1X_T:
                fontSel = Fonts.u8g2_font_open_iconic_www_1x_t;
                break;
            case FONT_OPEN_ICONIC_ALL_2X_T:
                fontSel = Fonts.u8g2_font_open_iconic_all_2x_t;
                break;
            case FONT_OPEN_ICONIC_APP_2X_T:
                fontSel = Fonts.u8g2_font_open_iconic_app_2x_t;
                break;
            case FONT_OPEN_ICONIC_ARROW_2X_T:
                fontSel = Fonts.u8g2_font_open_iconic_arrow_2x_t;
                break;
            case FONT_OPEN_ICONIC_CHECK_2X_T:
                fontSel = Fonts.u8g2_font_open_iconic_check_2x_t;
                break;
            case FONT_OPEN_ICONIC_EMAIL_2X_T:
                fontSel = Fonts.u8g2_font_open_iconic_email_2x_t;
                break;
            case FONT_OPEN_ICONIC_EMBEDDED_2X_T:
                fontSel = Fonts.u8g2_font_open_iconic_embedded_2x_t;
                break;
            case FONT_OPEN_ICONIC_GUI_2X_T:
                fontSel = Fonts.u8g2_font_open_iconic_gui_2x_t;
                break;
            case FONT_OPEN_ICONIC_HUMAN_2X_T:
                fontSel = Fonts.u8g2_font_open_iconic_human_2x_t;
                break;
            case FONT_OPEN_ICONIC_MIME_2X_T:
                fontSel = Fonts.u8g2_font_open_iconic_mime_2x_t;
                break;
            case FONT_OPEN_ICONIC_OTHER_2X_T:
                fontSel = Fonts.u8g2_font_open_iconic_other_2x_t;
                break;
            case FONT_OPEN_ICONIC_PLAY_2X_T:
                fontSel = Fonts.u8g2_font_open_iconic_play_2x_t;
                break;
            case FONT_OPEN_ICONIC_TEXT_2X_T:
                fontSel = Fonts.u8g2_font_open_iconic_text_2x_t;
                break;
            case FONT_OPEN_ICONIC_THING_2X_T:
                fontSel = Fonts.u8g2_font_open_iconic_thing_2x_t;
                break;
            case FONT_OPEN_ICONIC_WEATHER_2X_T:
                fontSel = Fonts.u8g2_font_open_iconic_weather_2x_t;
                break;
            case FONT_OPEN_ICONIC_WWW_2X_T:
                fontSel = Fonts.u8g2_font_open_iconic_www_2x_t;
                break;
            case FONT_OPEN_ICONIC_ALL_4X_T:
                fontSel = Fonts.u8g2_font_open_iconic_all_4x_t;
                break;
            case FONT_OPEN_ICONIC_APP_4X_T:
                fontSel = Fonts.u8g2_font_open_iconic_app_4x_t;
                break;
            case FONT_OPEN_ICONIC_ARROW_4X_T:
                fontSel = Fonts.u8g2_font_open_iconic_arrow_4x_t;
                break;
            case FONT_OPEN_ICONIC_CHECK_4X_T:
                fontSel = Fonts.u8g2_font_open_iconic_check_4x_t;
                break;
            case FONT_OPEN_ICONIC_EMAIL_4X_T:
                fontSel = Fonts.u8g2_font_open_iconic_email_4x_t;
                break;
            case FONT_OPEN_ICONIC_EMBEDDED_4X_T:
                fontSel = Fonts.u8g2_font_open_iconic_embedded_4x_t;
                break;
            case FONT_OPEN_ICONIC_GUI_4X_T:
                fontSel = Fonts.u8g2_font_open_iconic_gui_4x_t;
                break;
            case FONT_OPEN_ICONIC_HUMAN_4X_T:
                fontSel = Fonts.u8g2_font_open_iconic_human_4x_t;
                break;
            case FONT_OPEN_ICONIC_MIME_4X_T:
                fontSel = Fonts.u8g2_font_open_iconic_mime_4x_t;
                break;
            case FONT_OPEN_ICONIC_OTHER_4X_T:
                fontSel = Fonts.u8g2_font_open_iconic_other_4x_t;
                break;
            case FONT_OPEN_ICONIC_PLAY_4X_T:
                fontSel = Fonts.u8g2_font_open_iconic_play_4x_t;
                break;
            case FONT_OPEN_ICONIC_TEXT_4X_T:
                fontSel = Fonts.u8g2_font_open_iconic_text_4x_t;
                break;
            case FONT_OPEN_ICONIC_THING_4X_T:
                fontSel = Fonts.u8g2_font_open_iconic_thing_4x_t;
                break;
            case FONT_OPEN_ICONIC_WEATHER_4X_T:
                fontSel = Fonts.u8g2_font_open_iconic_weather_4x_t;
                break;
            case FONT_OPEN_ICONIC_WWW_4X_T:
                fontSel = Fonts.u8g2_font_open_iconic_www_4x_t;
                break;
            case FONT_OPEN_ICONIC_ALL_6X_T:
                fontSel = Fonts.u8g2_font_open_iconic_all_6x_t;
                break;
            case FONT_OPEN_ICONIC_APP_6X_T:
                fontSel = Fonts.u8g2_font_open_iconic_app_6x_t;
                break;
            case FONT_OPEN_ICONIC_ARROW_6X_T:
                fontSel = Fonts.u8g2_font_open_iconic_arrow_6x_t;
                break;
            case FONT_OPEN_ICONIC_CHECK_6X_T:
                fontSel = Fonts.u8g2_font_open_iconic_check_6x_t;
                break;
            case FONT_OPEN_ICONIC_EMAIL_6X_T:
                fontSel = Fonts.u8g2_font_open_iconic_email_6x_t;
                break;
            case FONT_OPEN_ICONIC_EMBEDDED_6X_T:
                fontSel = Fonts.u8g2_font_open_iconic_embedded_6x_t;
                break;
            case FONT_OPEN_ICONIC_GUI_6X_T:
                fontSel = Fonts.u8g2_font_open_iconic_gui_6x_t;
                break;
            case FONT_OPEN_ICONIC_HUMAN_6X_T:
                fontSel = Fonts.u8g2_font_open_iconic_human_6x_t;
                break;
            case FONT_OPEN_ICONIC_MIME_6X_T:
                fontSel = Fonts.u8g2_font_open_iconic_mime_6x_t;
                break;
            case FONT_OPEN_ICONIC_OTHER_6X_T:
                fontSel = Fonts.u8g2_font_open_iconic_other_6x_t;
                break;
            case FONT_OPEN_ICONIC_PLAY_6X_T:
                fontSel = Fonts.u8g2_font_open_iconic_play_6x_t;
                break;
            case FONT_OPEN_ICONIC_TEXT_6X_T:
                fontSel = Fonts.u8g2_font_open_iconic_text_6x_t;
                break;
            case FONT_OPEN_ICONIC_THING_6X_T:
                fontSel = Fonts.u8g2_font_open_iconic_thing_6x_t;
                break;
            case FONT_OPEN_ICONIC_WEATHER_6X_T:
                fontSel = Fonts.u8g2_font_open_iconic_weather_6x_t;
                break;
            case FONT_OPEN_ICONIC_WWW_6X_T:
                fontSel = Fonts.u8g2_font_open_iconic_www_6x_t;
                break;
            case FONT_OPEN_ICONIC_ALL_8X_T:
                fontSel = Fonts.u8g2_font_open_iconic_all_8x_t;
                break;
            case FONT_OPEN_ICONIC_APP_8X_T:
                fontSel = Fonts.u8g2_font_open_iconic_app_8x_t;
                break;
            case FONT_OPEN_ICONIC_ARROW_8X_T:
                fontSel = Fonts.u8g2_font_open_iconic_arrow_8x_t;
                break;
            case FONT_OPEN_ICONIC_CHECK_8X_T:
                fontSel = Fonts.u8g2_font_open_iconic_check_8x_t;
                break;
            case FONT_OPEN_ICONIC_EMAIL_8X_T:
                fontSel = Fonts.u8g2_font_open_iconic_email_8x_t;
                break;
            case FONT_OPEN_ICONIC_EMBEDDED_8X_T:
                fontSel = Fonts.u8g2_font_open_iconic_embedded_8x_t;
                break;
            case FONT_OPEN_ICONIC_GUI_8X_T:
                fontSel = Fonts.u8g2_font_open_iconic_gui_8x_t;
                break;
            case FONT_OPEN_ICONIC_HUMAN_8X_T:
                fontSel = Fonts.u8g2_font_open_iconic_human_8x_t;
                break;
            case FONT_OPEN_ICONIC_MIME_8X_T:
                fontSel = Fonts.u8g2_font_open_iconic_mime_8x_t;
                break;
            case FONT_OPEN_ICONIC_OTHER_8X_T:
                fontSel = Fonts.u8g2_font_open_iconic_other_8x_t;
                break;
            case FONT_OPEN_ICONIC_PLAY_8X_T:
                fontSel = Fonts.u8g2_font_open_iconic_play_8x_t;
                break;
            case FONT_OPEN_ICONIC_TEXT_8X_T:
                fontSel = Fonts.u8g2_font_open_iconic_text_8x_t;
                break;
            case FONT_OPEN_ICONIC_THING_8X_T:
                fontSel = Fonts.u8g2_font_open_iconic_thing_8x_t;
                break;
            case FONT_OPEN_ICONIC_WEATHER_8X_T:
                fontSel = Fonts.u8g2_font_open_iconic_weather_8x_t;
                break;
            case FONT_OPEN_ICONIC_WWW_8X_T:
                fontSel = Fonts.u8g2_font_open_iconic_www_8x_t;
                break;
            case FONT_STREAMLINE_ALL_T:
                fontSel = Fonts.u8g2_font_streamline_all_t;
                break;
            case FONT_STREAMLINE_BUILDING_REAL_ESTATE_T:
                fontSel = Fonts.u8g2_font_streamline_building_real_estate_t;
                break;
            case FONT_STREAMLINE_BUSINESS_T:
                fontSel = Fonts.u8g2_font_streamline_business_t;
                break;
            case FONT_STREAMLINE_CODING_APPS_WEBSITES_T:
                fontSel = Fonts.u8g2_font_streamline_coding_apps_websites_t;
                break;
            case FONT_STREAMLINE_COMPUTERS_DEVICES_ELECTRONICS_T:
                fontSel = Fonts.u8g2_font_streamline_computers_devices_electronics_t;
                break;
            case FONT_STREAMLINE_CONTENT_FILES_T:
                fontSel = Fonts.u8g2_font_streamline_content_files_t;
                break;
            case FONT_STREAMLINE_DESIGN_T:
                fontSel = Fonts.u8g2_font_streamline_design_t;
                break;
            case FONT_STREAMLINE_ECOLOGY_T:
                fontSel = Fonts.u8g2_font_streamline_ecology_t;
                break;
            case FONT_STREAMLINE_EMAIL_T:
                fontSel = Fonts.u8g2_font_streamline_email_t;
                break;
            case FONT_STREAMLINE_ENTERTAINMENT_EVENTS_HOBBIES_T:
                fontSel = Fonts.u8g2_font_streamline_entertainment_events_hobbies_t;
                break;
            case FONT_STREAMLINE_FOOD_DRINK_T:
                fontSel = Fonts.u8g2_font_streamline_food_drink_t;
                break;
            case FONT_STREAMLINE_HAND_SIGNS_T:
                fontSel = Fonts.u8g2_font_streamline_hand_signs_t;
                break;
            case FONT_STREAMLINE_HEALTH_BEAUTY_T:
                fontSel = Fonts.u8g2_font_streamline_health_beauty_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_ACTION_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_action_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_ALERT_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_alert_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_AUDIO_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_audio_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_CALENDAR_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_calendar_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_CHART_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_chart_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_CIRCLE_TRIANGLE_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_circle_triangle_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_COG_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_cog_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_CURSOR_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_cursor_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_DIAL_PAD_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_dial_pad_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_EDIT_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_edit_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_EXPAND_SHRINK_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_expand_shrink_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_EYE_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_eye_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_FILE_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_file_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_HELP_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_help_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_HIERARCHY_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_hierarchy_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_HOME_MENU_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_home_menu_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_ID_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_id_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_KEY_LOCK_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_key_lock_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_LINK_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_link_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_LOADING_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_loading_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_LOGIN_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_login_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_OTHER_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_other_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_PAGINATE_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_paginate_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_SEARCH_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_search_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_SETTING_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_setting_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_SHARE_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_share_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_TEXT_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_text_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_WIFI_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_wifi_t;
                break;
            case FONT_STREAMLINE_INTERFACE_ESSENTIAL_ZOOM_T:
                fontSel = Fonts.u8g2_font_streamline_interface_essential_zoom_t;
                break;
            case FONT_STREAMLINE_INTERNET_NETWORK_T:
                fontSel = Fonts.u8g2_font_streamline_internet_network_t;
                break;
            case FONT_STREAMLINE_LOGO_T:
                fontSel = Fonts.u8g2_font_streamline_logo_t;
                break;
            case FONT_STREAMLINE_MAP_NAVIGATION_T:
                fontSel = Fonts.u8g2_font_streamline_map_navigation_t;
                break;
            case FONT_STREAMLINE_MONEY_PAYMENTS_T:
                fontSel = Fonts.u8g2_font_streamline_money_payments_t;
                break;
            case FONT_STREAMLINE_MUSIC_AUDIO_T:
                fontSel = Fonts.u8g2_font_streamline_music_audio_t;
                break;
            case FONT_STREAMLINE_PET_ANIMALS_T:
                fontSel = Fonts.u8g2_font_streamline_pet_animals_t;
                break;
            case FONT_STREAMLINE_PHONE_T:
                fontSel = Fonts.u8g2_font_streamline_phone_t;
                break;
            case FONT_STREAMLINE_PHOTOGRAPHY_T:
                fontSel = Fonts.u8g2_font_streamline_photography_t;
                break;
            case FONT_STREAMLINE_ROMANCE_T:
                fontSel = Fonts.u8g2_font_streamline_romance_t;
                break;
            case FONT_STREAMLINE_SCHOOL_SCIENCE_T:
                fontSel = Fonts.u8g2_font_streamline_school_science_t;
                break;
            case FONT_STREAMLINE_SHOPPING_SHIPPING_T:
                fontSel = Fonts.u8g2_font_streamline_shopping_shipping_t;
                break;
            case FONT_STREAMLINE_SOCIAL_REWARDS_T:
                fontSel = Fonts.u8g2_font_streamline_social_rewards_t;
                break;
            case FONT_STREAMLINE_TECHNOLOGY_T:
                fontSel = Fonts.u8g2_font_streamline_technology_t;
                break;
            case FONT_STREAMLINE_TRANSPORTATION_T:
                fontSel = Fonts.u8g2_font_streamline_transportation_t;
                break;
            case FONT_STREAMLINE_TRAVEL_WAYFINDING_T:
                fontSel = Fonts.u8g2_font_streamline_travel_wayfinding_t;
                break;
            case FONT_STREAMLINE_USERS_T:
                fontSel = Fonts.u8g2_font_streamline_users_t;
                break;
            case FONT_STREAMLINE_VIDEO_MOVIES_T:
                fontSel = Fonts.u8g2_font_streamline_video_movies_t;
                break;
            case FONT_STREAMLINE_WEATHER_T:
                fontSel = Fonts.u8g2_font_streamline_weather_t;
                break;
            case FONT_PROFONT10_TF:
                fontSel = Fonts.u8g2_font_profont10_tf;
                break;
            case FONT_PROFONT10_TR:
                fontSel = Fonts.u8g2_font_profont10_tr;
                break;
            case FONT_PROFONT10_TN:
                fontSel = Fonts.u8g2_font_profont10_tn;
                break;
            case FONT_PROFONT10_MF:
                fontSel = Fonts.u8g2_font_profont10_mf;
                break;
            case FONT_PROFONT10_MR:
                fontSel = Fonts.u8g2_font_profont10_mr;
                break;
            case FONT_PROFONT10_MN:
                fontSel = Fonts.u8g2_font_profont10_mn;
                break;
            case FONT_PROFONT11_TF:
                fontSel = Fonts.u8g2_font_profont11_tf;
                break;
            case FONT_PROFONT11_TR:
                fontSel = Fonts.u8g2_font_profont11_tr;
                break;
            case FONT_PROFONT11_TN:
                fontSel = Fonts.u8g2_font_profont11_tn;
                break;
            case FONT_PROFONT11_MF:
                fontSel = Fonts.u8g2_font_profont11_mf;
                break;
            case FONT_PROFONT11_MR:
                fontSel = Fonts.u8g2_font_profont11_mr;
                break;
            case FONT_PROFONT11_MN:
                fontSel = Fonts.u8g2_font_profont11_mn;
                break;
            case FONT_PROFONT12_TF:
                fontSel = Fonts.u8g2_font_profont12_tf;
                break;
            case FONT_PROFONT12_TR:
                fontSel = Fonts.u8g2_font_profont12_tr;
                break;
            case FONT_PROFONT12_TN:
                fontSel = Fonts.u8g2_font_profont12_tn;
                break;
            case FONT_PROFONT12_MF:
                fontSel = Fonts.u8g2_font_profont12_mf;
                break;
            case FONT_PROFONT12_MR:
                fontSel = Fonts.u8g2_font_profont12_mr;
                break;
            case FONT_PROFONT12_MN:
                fontSel = Fonts.u8g2_font_profont12_mn;
                break;
            case FONT_PROFONT15_TF:
                fontSel = Fonts.u8g2_font_profont15_tf;
                break;
            case FONT_PROFONT15_TR:
                fontSel = Fonts.u8g2_font_profont15_tr;
                break;
            case FONT_PROFONT15_TN:
                fontSel = Fonts.u8g2_font_profont15_tn;
                break;
            case FONT_PROFONT15_MF:
                fontSel = Fonts.u8g2_font_profont15_mf;
                break;
            case FONT_PROFONT15_MR:
                fontSel = Fonts.u8g2_font_profont15_mr;
                break;
            case FONT_PROFONT15_MN:
                fontSel = Fonts.u8g2_font_profont15_mn;
                break;
            case FONT_PROFONT17_TF:
                fontSel = Fonts.u8g2_font_profont17_tf;
                break;
            case FONT_PROFONT17_TR:
                fontSel = Fonts.u8g2_font_profont17_tr;
                break;
            case FONT_PROFONT17_TN:
                fontSel = Fonts.u8g2_font_profont17_tn;
                break;
            case FONT_PROFONT17_MF:
                fontSel = Fonts.u8g2_font_profont17_mf;
                break;
            case FONT_PROFONT17_MR:
                fontSel = Fonts.u8g2_font_profont17_mr;
                break;
            case FONT_PROFONT17_MN:
                fontSel = Fonts.u8g2_font_profont17_mn;
                break;
            case FONT_PROFONT22_TF:
                fontSel = Fonts.u8g2_font_profont22_tf;
                break;
            case FONT_PROFONT22_TR:
                fontSel = Fonts.u8g2_font_profont22_tr;
                break;
            case FONT_PROFONT22_TN:
                fontSel = Fonts.u8g2_font_profont22_tn;
                break;
            case FONT_PROFONT22_MF:
                fontSel = Fonts.u8g2_font_profont22_mf;
                break;
            case FONT_PROFONT22_MR:
                fontSel = Fonts.u8g2_font_profont22_mr;
                break;
            case FONT_PROFONT22_MN:
                fontSel = Fonts.u8g2_font_profont22_mn;
                break;
            case FONT_PROFONT29_TF:
                fontSel = Fonts.u8g2_font_profont29_tf;
                break;
            case FONT_PROFONT29_TR:
                fontSel = Fonts.u8g2_font_profont29_tr;
                break;
            case FONT_PROFONT29_TN:
                fontSel = Fonts.u8g2_font_profont29_tn;
                break;
            case FONT_PROFONT29_MF:
                fontSel = Fonts.u8g2_font_profont29_mf;
                break;
            case FONT_PROFONT29_MR:
                fontSel = Fonts.u8g2_font_profont29_mr;
                break;
            case FONT_PROFONT29_MN:
                fontSel = Fonts.u8g2_font_profont29_mn;
                break;
            case FONT_SAMIM_10_T_ALL:
                fontSel = Fonts.u8g2_font_samim_10_t_all;
                break;
            case FONT_SAMIM_12_T_ALL:
                fontSel = Fonts.u8g2_font_samim_12_t_all;
                break;
            case FONT_SAMIM_14_T_ALL:
                fontSel = Fonts.u8g2_font_samim_14_t_all;
                break;
            case FONT_SAMIM_16_T_ALL:
                fontSel = Fonts.u8g2_font_samim_16_t_all;
                break;
            case FONT_SAMIM_FD_10_T_ALL:
                fontSel = Fonts.u8g2_font_samim_fd_10_t_all;
                break;
            case FONT_SAMIM_FD_12_T_ALL:
                fontSel = Fonts.u8g2_font_samim_fd_12_t_all;
                break;
            case FONT_SAMIM_FD_14_T_ALL:
                fontSel = Fonts.u8g2_font_samim_fd_14_t_all;
                break;
            case FONT_SAMIM_FD_16_T_ALL:
                fontSel = Fonts.u8g2_font_samim_fd_16_t_all;
                break;
            case FONT_GANJ_NAMEH_SANS10_T_ALL:
                fontSel = Fonts.u8g2_font_ganj_nameh_sans10_t_all;
                break;
            case FONT_GANJ_NAMEH_SANS12_T_ALL:
                fontSel = Fonts.u8g2_font_ganj_nameh_sans12_t_all;
                break;
            case FONT_GANJ_NAMEH_SANS14_T_ALL:
                fontSel = Fonts.u8g2_font_ganj_nameh_sans14_t_all;
                break;
            case FONT_GANJ_NAMEH_SANS16_T_ALL:
                fontSel = Fonts.u8g2_font_ganj_nameh_sans16_t_all;
                break;
            case FONT_IRANIAN_SANS_8_T_ALL:
                fontSel = Fonts.u8g2_font_iranian_sans_8_t_all;
                break;
            case FONT_IRANIAN_SANS_10_T_ALL:
                fontSel = Fonts.u8g2_font_iranian_sans_10_t_all;
                break;
            case FONT_IRANIAN_SANS_12_T_ALL:
                fontSel = Fonts.u8g2_font_iranian_sans_12_t_all;
                break;
            case FONT_IRANIAN_SANS_14_T_ALL:
                fontSel = Fonts.u8g2_font_iranian_sans_14_t_all;
                break;
            case FONT_IRANIAN_SANS_16_T_ALL:
                fontSel = Fonts.u8g2_font_iranian_sans_16_t_all;
                break;
            case FONT_MOZART_NBP_TF:
                fontSel = Fonts.u8g2_font_mozart_nbp_tf;
                break;
            case FONT_MOZART_NBP_TR:
                fontSel = Fonts.u8g2_font_mozart_nbp_tr;
                break;
            case FONT_MOZART_NBP_TN:
                fontSel = Fonts.u8g2_font_mozart_nbp_tn;
                break;
            case FONT_MOZART_NBP_T_ALL:
                fontSel = Fonts.u8g2_font_mozart_nbp_t_all;
                break;
            case FONT_MOZART_NBP_H_ALL:
                fontSel = Fonts.u8g2_font_mozart_nbp_h_all;
                break;
            case FONT_GLASSTOWN_NBP_TF:
                fontSel = Fonts.u8g2_font_glasstown_nbp_tf;
                break;
            case FONT_GLASSTOWN_NBP_TR:
                fontSel = Fonts.u8g2_font_glasstown_nbp_tr;
                break;
            case FONT_GLASSTOWN_NBP_TN:
                fontSel = Fonts.u8g2_font_glasstown_nbp_tn;
                break;
            case FONT_GLASSTOWN_NBP_T_ALL:
                fontSel = Fonts.u8g2_font_glasstown_nbp_t_all;
                break;
            case FONT_SHYLOCK_NBP_TF:
                fontSel = Fonts.u8g2_font_shylock_nbp_tf;
                break;
            case FONT_SHYLOCK_NBP_TR:
                fontSel = Fonts.u8g2_font_shylock_nbp_tr;
                break;
            case FONT_SHYLOCK_NBP_TN:
                fontSel = Fonts.u8g2_font_shylock_nbp_tn;
                break;
            case FONT_SHYLOCK_NBP_T_ALL:
                fontSel = Fonts.u8g2_font_shylock_nbp_t_all;
                break;
            case FONT_ROENTGEN_NBP_TF:
                fontSel = Fonts.u8g2_font_roentgen_nbp_tf;
                break;
            case FONT_ROENTGEN_NBP_TR:
                fontSel = Fonts.u8g2_font_roentgen_nbp_tr;
                break;
            case FONT_ROENTGEN_NBP_TN:
                fontSel = Fonts.u8g2_font_roentgen_nbp_tn;
                break;
            case FONT_ROENTGEN_NBP_T_ALL:
                fontSel = Fonts.u8g2_font_roentgen_nbp_t_all;
                break;
            case FONT_ROENTGEN_NBP_H_ALL:
                fontSel = Fonts.u8g2_font_roentgen_nbp_h_all;
                break;
            case FONT_CALIBRATION_GOTHIC_NBP_TF:
                fontSel = Fonts.u8g2_font_calibration_gothic_nbp_tf;
                break;
            case FONT_CALIBRATION_GOTHIC_NBP_TR:
                fontSel = Fonts.u8g2_font_calibration_gothic_nbp_tr;
                break;
            case FONT_CALIBRATION_GOTHIC_NBP_TN:
                fontSel = Fonts.u8g2_font_calibration_gothic_nbp_tn;
                break;
            case FONT_CALIBRATION_GOTHIC_NBP_T_ALL:
                fontSel = Fonts.u8g2_font_calibration_gothic_nbp_t_all;
                break;
            case FONT_SMART_PATROL_NBP_TF:
                fontSel = Fonts.u8g2_font_smart_patrol_nbp_tf;
                break;
            case FONT_SMART_PATROL_NBP_TR:
                fontSel = Fonts.u8g2_font_smart_patrol_nbp_tr;
                break;
            case FONT_SMART_PATROL_NBP_TN:
                fontSel = Fonts.u8g2_font_smart_patrol_nbp_tn;
                break;
            case FONT_PROSPERO_BOLD_NBP_TF:
                fontSel = Fonts.u8g2_font_prospero_bold_nbp_tf;
                break;
            case FONT_PROSPERO_BOLD_NBP_TR:
                fontSel = Fonts.u8g2_font_prospero_bold_nbp_tr;
                break;
            case FONT_PROSPERO_BOLD_NBP_TN:
                fontSel = Fonts.u8g2_font_prospero_bold_nbp_tn;
                break;
            case FONT_PROSPERO_NBP_TF:
                fontSel = Fonts.u8g2_font_prospero_nbp_tf;
                break;
            case FONT_PROSPERO_NBP_TR:
                fontSel = Fonts.u8g2_font_prospero_nbp_tr;
                break;
            case FONT_PROSPERO_NBP_TN:
                fontSel = Fonts.u8g2_font_prospero_nbp_tn;
                break;
            case FONT_BALTHASAR_REGULAR_NBP_TF:
                fontSel = Fonts.u8g2_font_balthasar_regular_nbp_tf;
                break;
            case FONT_BALTHASAR_REGULAR_NBP_TR:
                fontSel = Fonts.u8g2_font_balthasar_regular_nbp_tr;
                break;
            case FONT_BALTHASAR_REGULAR_NBP_TN:
                fontSel = Fonts.u8g2_font_balthasar_regular_nbp_tn;
                break;
            case FONT_BALTHASAR_TITLING_NBP_TF:
                fontSel = Fonts.u8g2_font_balthasar_titling_nbp_tf;
                break;
            case FONT_BALTHASAR_TITLING_NBP_TR:
                fontSel = Fonts.u8g2_font_balthasar_titling_nbp_tr;
                break;
            case FONT_BALTHASAR_TITLING_NBP_TN:
                fontSel = Fonts.u8g2_font_balthasar_titling_nbp_tn;
                break;
            case FONT_SYNCHRONIZER_NBP_TF:
                fontSel = Fonts.u8g2_font_synchronizer_nbp_tf;
                break;
            case FONT_SYNCHRONIZER_NBP_TR:
                fontSel = Fonts.u8g2_font_synchronizer_nbp_tr;
                break;
            case FONT_SYNCHRONIZER_NBP_TN:
                fontSel = Fonts.u8g2_font_synchronizer_nbp_tn;
                break;
            case FONT_MERCUTIO_BASIC_NBP_TF:
                fontSel = Fonts.u8g2_font_mercutio_basic_nbp_tf;
                break;
            case FONT_MERCUTIO_BASIC_NBP_TR:
                fontSel = Fonts.u8g2_font_mercutio_basic_nbp_tr;
                break;
            case FONT_MERCUTIO_BASIC_NBP_TN:
                fontSel = Fonts.u8g2_font_mercutio_basic_nbp_tn;
                break;
            case FONT_MERCUTIO_BASIC_NBP_T_ALL:
                fontSel = Fonts.u8g2_font_mercutio_basic_nbp_t_all;
                break;
            case FONT_MERCUTIO_SC_NBP_TF:
                fontSel = Fonts.u8g2_font_mercutio_sc_nbp_tf;
                break;
            case FONT_MERCUTIO_SC_NBP_TR:
                fontSel = Fonts.u8g2_font_mercutio_sc_nbp_tr;
                break;
            case FONT_MERCUTIO_SC_NBP_TN:
                fontSel = Fonts.u8g2_font_mercutio_sc_nbp_tn;
                break;
            case FONT_MERCUTIO_SC_NBP_T_ALL:
                fontSel = Fonts.u8g2_font_mercutio_sc_nbp_t_all;
                break;
            case FONT_MIRANDA_NBP_TF:
                fontSel = Fonts.u8g2_font_miranda_nbp_tf;
                break;
            case FONT_MIRANDA_NBP_TR:
                fontSel = Fonts.u8g2_font_miranda_nbp_tr;
                break;
            case FONT_MIRANDA_NBP_TN:
                fontSel = Fonts.u8g2_font_miranda_nbp_tn;
                break;
            case FONT_NINE_BY_FIVE_NBP_TF:
                fontSel = Fonts.u8g2_font_nine_by_five_nbp_tf;
                break;
            case FONT_NINE_BY_FIVE_NBP_TR:
                fontSel = Fonts.u8g2_font_nine_by_five_nbp_tr;
                break;
            case FONT_NINE_BY_FIVE_NBP_TN:
                fontSel = Fonts.u8g2_font_nine_by_five_nbp_tn;
                break;
            case FONT_NINE_BY_FIVE_NBP_T_ALL:
                fontSel = Fonts.u8g2_font_nine_by_five_nbp_t_all;
                break;
            case FONT_ROSENCRANTZ_NBP_TF:
                fontSel = Fonts.u8g2_font_rosencrantz_nbp_tf;
                break;
            case FONT_ROSENCRANTZ_NBP_TR:
                fontSel = Fonts.u8g2_font_rosencrantz_nbp_tr;
                break;
            case FONT_ROSENCRANTZ_NBP_TN:
                fontSel = Fonts.u8g2_font_rosencrantz_nbp_tn;
                break;
            case FONT_ROSENCRANTZ_NBP_T_ALL:
                fontSel = Fonts.u8g2_font_rosencrantz_nbp_t_all;
                break;
            case FONT_GUILDENSTERN_NBP_TF:
                fontSel = Fonts.u8g2_font_guildenstern_nbp_tf;
                break;
            case FONT_GUILDENSTERN_NBP_TR:
                fontSel = Fonts.u8g2_font_guildenstern_nbp_tr;
                break;
            case FONT_GUILDENSTERN_NBP_TN:
                fontSel = Fonts.u8g2_font_guildenstern_nbp_tn;
                break;
            case FONT_GUILDENSTERN_NBP_T_ALL:
                fontSel = Fonts.u8g2_font_guildenstern_nbp_t_all;
                break;
            case FONT_ASTRAGAL_NBP_TF:
                fontSel = Fonts.u8g2_font_astragal_nbp_tf;
                break;
            case FONT_ASTRAGAL_NBP_TR:
                fontSel = Fonts.u8g2_font_astragal_nbp_tr;
                break;
            case FONT_ASTRAGAL_NBP_TN:
                fontSel = Fonts.u8g2_font_astragal_nbp_tn;
                break;
            case FONT_HABSBURGCHANCERY_TF:
                fontSel = Fonts.u8g2_font_habsburgchancery_tf;
                break;
            case FONT_HABSBURGCHANCERY_TR:
                fontSel = Fonts.u8g2_font_habsburgchancery_tr;
                break;
            case FONT_HABSBURGCHANCERY_TN:
                fontSel = Fonts.u8g2_font_habsburgchancery_tn;
                break;
            case FONT_HABSBURGCHANCERY_T_ALL:
                fontSel = Fonts.u8g2_font_habsburgchancery_t_all;
                break;
            case FONT_MISSINGPLANET_TF:
                fontSel = Fonts.u8g2_font_missingplanet_tf;
                break;
            case FONT_MISSINGPLANET_TR:
                fontSel = Fonts.u8g2_font_missingplanet_tr;
                break;
            case FONT_MISSINGPLANET_TN:
                fontSel = Fonts.u8g2_font_missingplanet_tn;
                break;
            case FONT_MISSINGPLANET_T_ALL:
                fontSel = Fonts.u8g2_font_missingplanet_t_all;
                break;
            case FONT_ORDINARYBASIS_TF:
                fontSel = Fonts.u8g2_font_ordinarybasis_tf;
                break;
            case FONT_ORDINARYBASIS_TR:
                fontSel = Fonts.u8g2_font_ordinarybasis_tr;
                break;
            case FONT_ORDINARYBASIS_TN:
                fontSel = Fonts.u8g2_font_ordinarybasis_tn;
                break;
            case FONT_ORDINARYBASIS_T_ALL:
                fontSel = Fonts.u8g2_font_ordinarybasis_t_all;
                break;
            case FONT_PIXELMORDRED_TF:
                fontSel = Fonts.u8g2_font_pixelmordred_tf;
                break;
            case FONT_PIXELMORDRED_TR:
                fontSel = Fonts.u8g2_font_pixelmordred_tr;
                break;
            case FONT_PIXELMORDRED_TN:
                fontSel = Fonts.u8g2_font_pixelmordred_tn;
                break;
            case FONT_PIXELMORDRED_T_ALL:
                fontSel = Fonts.u8g2_font_pixelmordred_t_all;
                break;
            case FONT_SECRETARYHAND_TF:
                fontSel = Fonts.u8g2_font_secretaryhand_tf;
                break;
            case FONT_SECRETARYHAND_TR:
                fontSel = Fonts.u8g2_font_secretaryhand_tr;
                break;
            case FONT_SECRETARYHAND_TN:
                fontSel = Fonts.u8g2_font_secretaryhand_tn;
                break;
            case FONT_SECRETARYHAND_T_ALL:
                fontSel = Fonts.u8g2_font_secretaryhand_t_all;
                break;
            case FONT_BEANSTALK_MEL_TR:
                fontSel = Fonts.u8g2_font_beanstalk_mel_tr;
                break;
            case FONT_BEANSTALK_MEL_TN:
                fontSel = Fonts.u8g2_font_beanstalk_mel_tn;
                break;
            case FONT_CUBE_MEL_TR:
                fontSel = Fonts.u8g2_font_cube_mel_tr;
                break;
            case FONT_CUBE_MEL_TN:
                fontSel = Fonts.u8g2_font_cube_mel_tn;
                break;
            case FONT_MADEMOISELLE_MEL_TR:
                fontSel = Fonts.u8g2_font_mademoiselle_mel_tr;
                break;
            case FONT_MADEMOISELLE_MEL_TN:
                fontSel = Fonts.u8g2_font_mademoiselle_mel_tn;
                break;
            case FONT_PIECEOFCAKE_MEL_TR:
                fontSel = Fonts.u8g2_font_pieceofcake_mel_tr;
                break;
            case FONT_PIECEOFCAKE_MEL_TN:
                fontSel = Fonts.u8g2_font_pieceofcake_mel_tn;
                break;
            case FONT_PRESS_MEL_TR:
                fontSel = Fonts.u8g2_font_press_mel_tr;
                break;
            case FONT_PRESS_MEL_TN:
                fontSel = Fonts.u8g2_font_press_mel_tn;
                break;
            case FONT_REPRESS_MEL_TR:
                fontSel = Fonts.u8g2_font_repress_mel_tr;
                break;
            case FONT_REPRESS_MEL_TN:
                fontSel = Fonts.u8g2_font_repress_mel_tn;
                break;
            case FONT_STICKER_MEL_TR:
                fontSel = Fonts.u8g2_font_sticker_mel_tr;
                break;
            case FONT_STICKER_MEL_TN:
                fontSel = Fonts.u8g2_font_sticker_mel_tn;
                break;
            case FONT_CELIBATEMONK_TR:
                fontSel = Fonts.u8g2_font_celibatemonk_tr;
                break;
            case FONT_DISRESPECTFULTEENAGER_TU:
                fontSel = Fonts.u8g2_font_disrespectfulteenager_tu;
                break;
            case FONT_MICHAELMOUSE_TU:
                fontSel = Fonts.u8g2_font_michaelmouse_tu;
                break;
            case FONT_SANDYFOREST_TR:
                fontSel = Fonts.u8g2_font_sandyforest_tr;
                break;
            case FONT_SANDYFOREST_TN:
                fontSel = Fonts.u8g2_font_sandyforest_tn;
                break;
            case FONT_SANDYFOREST_TU:
                fontSel = Fonts.u8g2_font_sandyforest_tu;
                break;
            case FONT_CUPCAKEMETOYOURLEADER_TR:
                fontSel = Fonts.u8g2_font_cupcakemetoyourleader_tr;
                break;
            case FONT_CUPCAKEMETOYOURLEADER_TN:
                fontSel = Fonts.u8g2_font_cupcakemetoyourleader_tn;
                break;
            case FONT_CUPCAKEMETOYOURLEADER_TU:
                fontSel = Fonts.u8g2_font_cupcakemetoyourleader_tu;
                break;
            case FONT_OLDWIZARD_TF:
                fontSel = Fonts.u8g2_font_oldwizard_tf;
                break;
            case FONT_OLDWIZARD_TR:
                fontSel = Fonts.u8g2_font_oldwizard_tr;
                break;
            case FONT_OLDWIZARD_TN:
                fontSel = Fonts.u8g2_font_oldwizard_tn;
                break;
            case FONT_OLDWIZARD_TU:
                fontSel = Fonts.u8g2_font_oldwizard_tu;
                break;
            case FONT_SQUIRREL_TR:
                fontSel = Fonts.u8g2_font_squirrel_tr;
                break;
            case FONT_SQUIRREL_TN:
                fontSel = Fonts.u8g2_font_squirrel_tn;
                break;
            case FONT_SQUIRREL_TU:
                fontSel = Fonts.u8g2_font_squirrel_tu;
                break;
            case FONT_DIODESEMIMONO_TR:
                fontSel = Fonts.u8g2_font_diodesemimono_tr;
                break;
            case FONT_QUESTGIVER_TR:
                fontSel = Fonts.u8g2_font_questgiver_tr;
                break;
            case FONT_SERAPHIMB1_TR:
                fontSel = Fonts.u8g2_font_seraphimb1_tr;
                break;
            case FONT_JINXEDWIZARDS_TR:
                fontSel = Fonts.u8g2_font_jinxedwizards_tr;
                break;
            case FONT_LASTPRIESTESS_TR:
                fontSel = Fonts.u8g2_font_lastpriestess_tr;
                break;
            case FONT_LASTPRIESTESS_TU:
                fontSel = Fonts.u8g2_font_lastpriestess_tu;
                break;
            case FONT_BITCASUAL_TF:
                fontSel = Fonts.u8g2_font_bitcasual_tf;
                break;
            case FONT_BITCASUAL_TR:
                fontSel = Fonts.u8g2_font_bitcasual_tr;
                break;
            case FONT_BITCASUAL_TN:
                fontSel = Fonts.u8g2_font_bitcasual_tn;
                break;
            case FONT_BITCASUAL_TU:
                fontSel = Fonts.u8g2_font_bitcasual_tu;
                break;
            case FONT_BITCASUAL_T_ALL:
                fontSel = Fonts.u8g2_font_bitcasual_t_all;
                break;
            case FONT_KOLEEKO_TF:
                fontSel = Fonts.u8g2_font_koleeko_tf;
                break;
            case FONT_KOLEEKO_TR:
                fontSel = Fonts.u8g2_font_koleeko_tr;
                break;
            case FONT_KOLEEKO_TN:
                fontSel = Fonts.u8g2_font_koleeko_tn;
                break;
            case FONT_KOLEEKO_TU:
                fontSel = Fonts.u8g2_font_koleeko_tu;
                break;
            case FONT_TENFATGUYS_TF:
                fontSel = Fonts.u8g2_font_tenfatguys_tf;
                break;
            case FONT_TENFATGUYS_TR:
                fontSel = Fonts.u8g2_font_tenfatguys_tr;
                break;
            case FONT_TENFATGUYS_TN:
                fontSel = Fonts.u8g2_font_tenfatguys_tn;
                break;
            case FONT_TENFATGUYS_TU:
                fontSel = Fonts.u8g2_font_tenfatguys_tu;
                break;
            case FONT_TENFATGUYS_T_ALL:
                fontSel = Fonts.u8g2_font_tenfatguys_t_all;
                break;
            case FONT_TENSTAMPS_MF:
                fontSel = Fonts.u8g2_font_tenstamps_mf;
                break;
            case FONT_TENSTAMPS_MR:
                fontSel = Fonts.u8g2_font_tenstamps_mr;
                break;
            case FONT_TENSTAMPS_MN:
                fontSel = Fonts.u8g2_font_tenstamps_mn;
                break;
            case FONT_TENSTAMPS_MU:
                fontSel = Fonts.u8g2_font_tenstamps_mu;
                break;
            case FONT_TENTHINGUYS_TF:
                fontSel = Fonts.u8g2_font_tenthinguys_tf;
                break;
            case FONT_TENTHINGUYS_TR:
                fontSel = Fonts.u8g2_font_tenthinguys_tr;
                break;
            case FONT_TENTHINGUYS_TN:
                fontSel = Fonts.u8g2_font_tenthinguys_tn;
                break;
            case FONT_TENTHINGUYS_TU:
                fontSel = Fonts.u8g2_font_tenthinguys_tu;
                break;
            case FONT_TENTHINGUYS_T_ALL:
                fontSel = Fonts.u8g2_font_tenthinguys_t_all;
                break;
            case FONT_TENTHINNERGUYS_TF:
                fontSel = Fonts.u8g2_font_tenthinnerguys_tf;
                break;
            case FONT_TENTHINNERGUYS_TR:
                fontSel = Fonts.u8g2_font_tenthinnerguys_tr;
                break;
            case FONT_TENTHINNERGUYS_TN:
                fontSel = Fonts.u8g2_font_tenthinnerguys_tn;
                break;
            case FONT_TENTHINNERGUYS_TU:
                fontSel = Fonts.u8g2_font_tenthinnerguys_tu;
                break;
            case FONT_TENTHINNERGUYS_T_ALL:
                fontSel = Fonts.u8g2_font_tenthinnerguys_t_all;
                break;
            case FONT_TWELVEDINGS_T_ALL:
                fontSel = Fonts.u8g2_font_twelvedings_t_all;
                break;
            case FONT_FEWTURE_TF:
                fontSel = Fonts.u8g2_font_fewture_tf;
                break;
            case FONT_FEWTURE_TR:
                fontSel = Fonts.u8g2_font_fewture_tr;
                break;
            case FONT_FEWTURE_TN:
                fontSel = Fonts.u8g2_font_fewture_tn;
                break;
            case FONT_HALFTONE_TF:
                fontSel = Fonts.u8g2_font_halftone_tf;
                break;
            case FONT_HALFTONE_TR:
                fontSel = Fonts.u8g2_font_halftone_tr;
                break;
            case FONT_HALFTONE_TN:
                fontSel = Fonts.u8g2_font_halftone_tn;
                break;
            case FONT_NERHOE_TF:
                fontSel = Fonts.u8g2_font_nerhoe_tf;
                break;
            case FONT_NERHOE_TR:
                fontSel = Fonts.u8g2_font_nerhoe_tr;
                break;
            case FONT_NERHOE_TN:
                fontSel = Fonts.u8g2_font_nerhoe_tn;
                break;
            case FONT_OSKOOL_TF:
                fontSel = Fonts.u8g2_font_oskool_tf;
                break;
            case FONT_OSKOOL_TR:
                fontSel = Fonts.u8g2_font_oskool_tr;
                break;
            case FONT_OSKOOL_TN:
                fontSel = Fonts.u8g2_font_oskool_tn;
                break;
            case FONT_TINYTIM_TF:
                fontSel = Fonts.u8g2_font_tinytim_tf;
                break;
            case FONT_TINYTIM_TR:
                fontSel = Fonts.u8g2_font_tinytim_tr;
                break;
            case FONT_TINYTIM_TN:
                fontSel = Fonts.u8g2_font_tinytim_tn;
                break;
            case FONT_TOOSEORNAMENT_TF:
                fontSel = Fonts.u8g2_font_tooseornament_tf;
                break;
            case FONT_TOOSEORNAMENT_TR:
                fontSel = Fonts.u8g2_font_tooseornament_tr;
                break;
            case FONT_TOOSEORNAMENT_TN:
                fontSel = Fonts.u8g2_font_tooseornament_tn;
                break;
            case FONT_BAUHAUS2015_TR:
                fontSel = Fonts.u8g2_font_bauhaus2015_tr;
                break;
            case FONT_BAUHAUS2015_TN:
                fontSel = Fonts.u8g2_font_bauhaus2015_tn;
                break;
            case FONT_FINDERSKEEPERS_TF:
                fontSel = Fonts.u8g2_font_finderskeepers_tf;
                break;
            case FONT_FINDERSKEEPERS_TR:
                fontSel = Fonts.u8g2_font_finderskeepers_tr;
                break;
            case FONT_FINDERSKEEPERS_TN:
                fontSel = Fonts.u8g2_font_finderskeepers_tn;
                break;
            case FONT_SIRCLIVETHEBOLD_TR:
                fontSel = Fonts.u8g2_font_sirclivethebold_tr;
                break;
            case FONT_SIRCLIVETHEBOLD_TN:
                fontSel = Fonts.u8g2_font_sirclivethebold_tn;
                break;
            case FONT_SIRCLIVE_TR:
                fontSel = Fonts.u8g2_font_sirclive_tr;
                break;
            case FONT_SIRCLIVE_TN:
                fontSel = Fonts.u8g2_font_sirclive_tn;
                break;
            case FONT_ADVENTURER_TF:
                fontSel = Fonts.u8g2_font_adventurer_tf;
                break;
            case FONT_ADVENTURER_TR:
                fontSel = Fonts.u8g2_font_adventurer_tr;
                break;
            case FONT_ADVENTURER_T_ALL:
                fontSel = Fonts.u8g2_font_adventurer_t_all;
                break;
            case FONT_BRACKETEDBABIES_TR:
                fontSel = Fonts.u8g2_font_bracketedbabies_tr;
                break;
            case FONT_FRIKATIV_TF:
                fontSel = Fonts.u8g2_font_frikativ_tf;
                break;
            case FONT_FRIKATIV_TR:
                fontSel = Fonts.u8g2_font_frikativ_tr;
                break;
            case FONT_FRIKATIV_T_ALL:
                fontSel = Fonts.u8g2_font_frikativ_t_all;
                break;
            case FONT_FANCYPIXELS_TF:
                fontSel = Fonts.u8g2_font_fancypixels_tf;
                break;
            case FONT_FANCYPIXELS_TR:
                fontSel = Fonts.u8g2_font_fancypixels_tr;
                break;
            case FONT_HEAVYBOTTOM_TR:
                fontSel = Fonts.u8g2_font_heavybottom_tr;
                break;
            case FONT_ICONQUADPIX_M_ALL:
                fontSel = Fonts.u8g2_font_iconquadpix_m_all;
                break;
            case FONT_LASTAPPRENTICEBOLD_TR:
                fontSel = Fonts.u8g2_font_lastapprenticebold_tr;
                break;
            case FONT_LASTAPPRENTICETHIN_TR:
                fontSel = Fonts.u8g2_font_lastapprenticethin_tr;
                break;
            case FONT_TALLPIX_TR:
                fontSel = Fonts.u8g2_font_tallpix_tr;
                break;
            case FONT_BBSESQUE_TF:
                fontSel = Fonts.u8g2_font_BBSesque_tf;
                break;
            case FONT_BBSESQUE_TR:
                fontSel = Fonts.u8g2_font_BBSesque_tr;
                break;
            case FONT_BBSESQUE_TE:
                fontSel = Fonts.u8g2_font_BBSesque_te;
                break;
            case FONT_BORN2BSPORTYSLAB_TF:
                fontSel = Fonts.u8g2_font_Born2bSportySlab_tf;
                break;
            case FONT_BORN2BSPORTYSLAB_TR:
                fontSel = Fonts.u8g2_font_Born2bSportySlab_tr;
                break;
            case FONT_BORN2BSPORTYSLAB_TE:
                fontSel = Fonts.u8g2_font_Born2bSportySlab_te;
                break;
            case FONT_BORN2BSPORTYSLAB_T_ALL:
                fontSel = Fonts.u8g2_font_Born2bSportySlab_t_all;
                break;
            case FONT_BORN2BSPORTYV2_TF:
                fontSel = Fonts.u8g2_font_Born2bSportyV2_tf;
                break;
            case FONT_BORN2BSPORTYV2_TR:
                fontSel = Fonts.u8g2_font_Born2bSportyV2_tr;
                break;
            case FONT_BORN2BSPORTYV2_TE:
                fontSel = Fonts.u8g2_font_Born2bSportyV2_te;
                break;
            case FONT_CURSIVEPIXEL_TR:
                fontSel = Fonts.u8g2_font_CursivePixel_tr;
                break;
            case FONT_ENGRISH_TF:
                fontSel = Fonts.u8g2_font_Engrish_tf;
                break;
            case FONT_ENGRISH_TR:
                fontSel = Fonts.u8g2_font_Engrish_tr;
                break;
            case FONT_IMPACTBITS_TR:
                fontSel = Fonts.u8g2_font_ImpactBits_tr;
                break;
            case FONT_IPAANDRUSLCD_TF:
                fontSel = Fonts.u8g2_font_IPAandRUSLCD_tf;
                break;
            case FONT_IPAANDRUSLCD_TR:
                fontSel = Fonts.u8g2_font_IPAandRUSLCD_tr;
                break;
            case FONT_IPAANDRUSLCD_TE:
                fontSel = Fonts.u8g2_font_IPAandRUSLCD_te;
                break;
            case FONT_HELVETIPIXEL_TR:
                fontSel = Fonts.u8g2_font_HelvetiPixel_tr;
                break;
            case FONT_TIMESNEWPIXEL_TR:
                fontSel = Fonts.u8g2_font_TimesNewPixel_tr;
                break;
            case FONT_BITTYPEWRITER_TR:
                fontSel = Fonts.u8g2_font_BitTypeWriter_tr;
                break;
            case FONT_BITTYPEWRITER_TE:
                fontSel = Fonts.u8g2_font_BitTypeWriter_te;
                break;
            case FONT_GEORGIA7PX_TF:
                fontSel = Fonts.u8g2_font_Georgia7px_tf;
                break;
            case FONT_GEORGIA7PX_TR:
                fontSel = Fonts.u8g2_font_Georgia7px_tr;
                break;
            case FONT_GEORGIA7PX_TE:
                fontSel = Fonts.u8g2_font_Georgia7px_te;
                break;
            case FONT_WIZZARD_TR:
                fontSel = Fonts.u8g2_font_Wizzard_tr;
                break;
            case FONT_HELVETIPIXELOUTLINE_TR:
                fontSel = Fonts.u8g2_font_HelvetiPixelOutline_tr;
                break;
            case FONT_HELVETIPIXELOUTLINE_TE:
                fontSel = Fonts.u8g2_font_HelvetiPixelOutline_te;
                break;
            case FONT_UNTITLED16PIXELSANSSERIFBITMAP_TR:
                fontSel = Fonts.u8g2_font_Untitled16PixelSansSerifBitmap_tr;
                break;
            case FONT_NOKIAFC22_TF:
                fontSel = Fonts.u8g2_font_nokiafc22_tf;
                break;
            case FONT_NOKIAFC22_TR:
                fontSel = Fonts.u8g2_font_nokiafc22_tr;
                break;
            case FONT_NOKIAFC22_TN:
                fontSel = Fonts.u8g2_font_nokiafc22_tn;
                break;
            case FONT_NOKIAFC22_TU:
                fontSel = Fonts.u8g2_font_nokiafc22_tu;
                break;
            case FONT_VCR_OSD_TF:
                fontSel = Fonts.u8g2_font_VCR_OSD_tf;
                break;
            case FONT_VCR_OSD_TR:
                fontSel = Fonts.u8g2_font_VCR_OSD_tr;
                break;
            case FONT_VCR_OSD_TN:
                fontSel = Fonts.u8g2_font_VCR_OSD_tn;
                break;
            case FONT_VCR_OSD_TU:
                fontSel = Fonts.u8g2_font_VCR_OSD_tu;
                break;
            case FONT_VCR_OSD_MF:
                fontSel = Fonts.u8g2_font_VCR_OSD_mf;
                break;
            case FONT_VCR_OSD_MR:
                fontSel = Fonts.u8g2_font_VCR_OSD_mr;
                break;
            case FONT_VCR_OSD_MN:
                fontSel = Fonts.u8g2_font_VCR_OSD_mn;
                break;
            case FONT_VCR_OSD_MU:
                fontSel = Fonts.u8g2_font_VCR_OSD_mu;
                break;
            case FONT_PIXELLARI_TF:
                fontSel = Fonts.u8g2_font_Pixellari_tf;
                break;
            case FONT_PIXELLARI_TR:
                fontSel = Fonts.u8g2_font_Pixellari_tr;
                break;
            case FONT_PIXELLARI_TN:
                fontSel = Fonts.u8g2_font_Pixellari_tn;
                break;
            case FONT_PIXELLARI_TU:
                fontSel = Fonts.u8g2_font_Pixellari_tu;
                break;
            case FONT_PIXELLARI_TE:
                fontSel = Fonts.u8g2_font_Pixellari_te;
                break;
            case FONT_PIXELPOIIZ_TR:
                fontSel = Fonts.u8g2_font_pixelpoiiz_tr;
                break;
            case FONT_DIGITALDISCOTHIN_TF:
                fontSel = Fonts.u8g2_font_DigitalDiscoThin_tf;
                break;
            case FONT_DIGITALDISCOTHIN_TR:
                fontSel = Fonts.u8g2_font_DigitalDiscoThin_tr;
                break;
            case FONT_DIGITALDISCOTHIN_TN:
                fontSel = Fonts.u8g2_font_DigitalDiscoThin_tn;
                break;
            case FONT_DIGITALDISCOTHIN_TU:
                fontSel = Fonts.u8g2_font_DigitalDiscoThin_tu;
                break;
            case FONT_DIGITALDISCOTHIN_TE:
                fontSel = Fonts.u8g2_font_DigitalDiscoThin_te;
                break;
            case FONT_DIGITALDISCO_TF:
                fontSel = Fonts.u8g2_font_DigitalDisco_tf;
                break;
            case FONT_DIGITALDISCO_TR:
                fontSel = Fonts.u8g2_font_DigitalDisco_tr;
                break;
            case FONT_DIGITALDISCO_TN:
                fontSel = Fonts.u8g2_font_DigitalDisco_tn;
                break;
            case FONT_DIGITALDISCO_TU:
                fontSel = Fonts.u8g2_font_DigitalDisco_tu;
                break;
            case FONT_DIGITALDISCO_TE:
                fontSel = Fonts.u8g2_font_DigitalDisco_te;
                break;
            case FONT_PEARFONT_TR:
                fontSel = Fonts.u8g2_font_pearfont_tr;
                break;
            case FONT_ETL14THAI_T:
                fontSel = Fonts.u8g2_font_etl14thai_t;
                break;
            case FONT_ETL16THAI_T:
                fontSel = Fonts.u8g2_font_etl16thai_t;
                break;
            case FONT_ETL24THAI_T:
                fontSel = Fonts.u8g2_font_etl24thai_t;
                break;
            case FONT_CROX1CB_TF:
                fontSel = Fonts.u8g2_font_crox1cb_tf;
                break;
            case FONT_CROX1CB_TR:
                fontSel = Fonts.u8g2_font_crox1cb_tr;
                break;
            case FONT_CROX1CB_TN:
                fontSel = Fonts.u8g2_font_crox1cb_tn;
                break;
            case FONT_CROX1CB_MF:
                fontSel = Fonts.u8g2_font_crox1cb_mf;
                break;
            case FONT_CROX1CB_MR:
                fontSel = Fonts.u8g2_font_crox1cb_mr;
                break;
            case FONT_CROX1CB_MN:
                fontSel = Fonts.u8g2_font_crox1cb_mn;
                break;
            case FONT_CROX1C_TF:
                fontSel = Fonts.u8g2_font_crox1c_tf;
                break;
            case FONT_CROX1C_TR:
                fontSel = Fonts.u8g2_font_crox1c_tr;
                break;
            case FONT_CROX1C_TN:
                fontSel = Fonts.u8g2_font_crox1c_tn;
                break;
            case FONT_CROX1C_MF:
                fontSel = Fonts.u8g2_font_crox1c_mf;
                break;
            case FONT_CROX1C_MR:
                fontSel = Fonts.u8g2_font_crox1c_mr;
                break;
            case FONT_CROX1C_MN:
                fontSel = Fonts.u8g2_font_crox1c_mn;
                break;
            case FONT_CROX1HB_TF:
                fontSel = Fonts.u8g2_font_crox1hb_tf;
                break;
            case FONT_CROX1HB_TR:
                fontSel = Fonts.u8g2_font_crox1hb_tr;
                break;
            case FONT_CROX1HB_TN:
                fontSel = Fonts.u8g2_font_crox1hb_tn;
                break;
            case FONT_CROX1H_TF:
                fontSel = Fonts.u8g2_font_crox1h_tf;
                break;
            case FONT_CROX1H_TR:
                fontSel = Fonts.u8g2_font_crox1h_tr;
                break;
            case FONT_CROX1H_TN:
                fontSel = Fonts.u8g2_font_crox1h_tn;
                break;
            case FONT_CROX1TB_TF:
                fontSel = Fonts.u8g2_font_crox1tb_tf;
                break;
            case FONT_CROX1TB_TR:
                fontSel = Fonts.u8g2_font_crox1tb_tr;
                break;
            case FONT_CROX1TB_TN:
                fontSel = Fonts.u8g2_font_crox1tb_tn;
                break;
            case FONT_CROX1T_TF:
                fontSel = Fonts.u8g2_font_crox1t_tf;
                break;
            case FONT_CROX1T_TR:
                fontSel = Fonts.u8g2_font_crox1t_tr;
                break;
            case FONT_CROX1T_TN:
                fontSel = Fonts.u8g2_font_crox1t_tn;
                break;
            case FONT_CROX2CB_TF:
                fontSel = Fonts.u8g2_font_crox2cb_tf;
                break;
            case FONT_CROX2CB_TR:
                fontSel = Fonts.u8g2_font_crox2cb_tr;
                break;
            case FONT_CROX2CB_TN:
                fontSel = Fonts.u8g2_font_crox2cb_tn;
                break;
            case FONT_CROX2CB_MF:
                fontSel = Fonts.u8g2_font_crox2cb_mf;
                break;
            case FONT_CROX2CB_MR:
                fontSel = Fonts.u8g2_font_crox2cb_mr;
                break;
            case FONT_CROX2CB_MN:
                fontSel = Fonts.u8g2_font_crox2cb_mn;
                break;
            case FONT_CROX2C_TF:
                fontSel = Fonts.u8g2_font_crox2c_tf;
                break;
            case FONT_CROX2C_TR:
                fontSel = Fonts.u8g2_font_crox2c_tr;
                break;
            case FONT_CROX2C_TN:
                fontSel = Fonts.u8g2_font_crox2c_tn;
                break;
            case FONT_CROX2C_MF:
                fontSel = Fonts.u8g2_font_crox2c_mf;
                break;
            case FONT_CROX2C_MR:
                fontSel = Fonts.u8g2_font_crox2c_mr;
                break;
            case FONT_CROX2C_MN:
                fontSel = Fonts.u8g2_font_crox2c_mn;
                break;
            case FONT_CROX2HB_TF:
                fontSel = Fonts.u8g2_font_crox2hb_tf;
                break;
            case FONT_CROX2HB_TR:
                fontSel = Fonts.u8g2_font_crox2hb_tr;
                break;
            case FONT_CROX2HB_TN:
                fontSel = Fonts.u8g2_font_crox2hb_tn;
                break;
            case FONT_CROX2H_TF:
                fontSel = Fonts.u8g2_font_crox2h_tf;
                break;
            case FONT_CROX2H_TR:
                fontSel = Fonts.u8g2_font_crox2h_tr;
                break;
            case FONT_CROX2H_TN:
                fontSel = Fonts.u8g2_font_crox2h_tn;
                break;
            case FONT_CROX2TB_TF:
                fontSel = Fonts.u8g2_font_crox2tb_tf;
                break;
            case FONT_CROX2TB_TR:
                fontSel = Fonts.u8g2_font_crox2tb_tr;
                break;
            case FONT_CROX2TB_TN:
                fontSel = Fonts.u8g2_font_crox2tb_tn;
                break;
            case FONT_CROX2T_TF:
                fontSel = Fonts.u8g2_font_crox2t_tf;
                break;
            case FONT_CROX2T_TR:
                fontSel = Fonts.u8g2_font_crox2t_tr;
                break;
            case FONT_CROX2T_TN:
                fontSel = Fonts.u8g2_font_crox2t_tn;
                break;
            case FONT_CROX3CB_TF:
                fontSel = Fonts.u8g2_font_crox3cb_tf;
                break;
            case FONT_CROX3CB_TR:
                fontSel = Fonts.u8g2_font_crox3cb_tr;
                break;
            case FONT_CROX3CB_TN:
                fontSel = Fonts.u8g2_font_crox3cb_tn;
                break;
            case FONT_CROX3CB_MF:
                fontSel = Fonts.u8g2_font_crox3cb_mf;
                break;
            case FONT_CROX3CB_MR:
                fontSel = Fonts.u8g2_font_crox3cb_mr;
                break;
            case FONT_CROX3CB_MN:
                fontSel = Fonts.u8g2_font_crox3cb_mn;
                break;
            case FONT_CROX3C_TF:
                fontSel = Fonts.u8g2_font_crox3c_tf;
                break;
            case FONT_CROX3C_TR:
                fontSel = Fonts.u8g2_font_crox3c_tr;
                break;
            case FONT_CROX3C_TN:
                fontSel = Fonts.u8g2_font_crox3c_tn;
                break;
            case FONT_CROX3C_MF:
                fontSel = Fonts.u8g2_font_crox3c_mf;
                break;
            case FONT_CROX3C_MR:
                fontSel = Fonts.u8g2_font_crox3c_mr;
                break;
            case FONT_CROX3C_MN:
                fontSel = Fonts.u8g2_font_crox3c_mn;
                break;
            case FONT_CROX3HB_TF:
                fontSel = Fonts.u8g2_font_crox3hb_tf;
                break;
            case FONT_CROX3HB_TR:
                fontSel = Fonts.u8g2_font_crox3hb_tr;
                break;
            case FONT_CROX3HB_TN:
                fontSel = Fonts.u8g2_font_crox3hb_tn;
                break;
            case FONT_CROX3H_TF:
                fontSel = Fonts.u8g2_font_crox3h_tf;
                break;
            case FONT_CROX3H_TR:
                fontSel = Fonts.u8g2_font_crox3h_tr;
                break;
            case FONT_CROX3H_TN:
                fontSel = Fonts.u8g2_font_crox3h_tn;
                break;
            case FONT_CROX3TB_TF:
                fontSel = Fonts.u8g2_font_crox3tb_tf;
                break;
            case FONT_CROX3TB_TR:
                fontSel = Fonts.u8g2_font_crox3tb_tr;
                break;
            case FONT_CROX3TB_TN:
                fontSel = Fonts.u8g2_font_crox3tb_tn;
                break;
            case FONT_CROX3T_TF:
                fontSel = Fonts.u8g2_font_crox3t_tf;
                break;
            case FONT_CROX3T_TR:
                fontSel = Fonts.u8g2_font_crox3t_tr;
                break;
            case FONT_CROX3T_TN:
                fontSel = Fonts.u8g2_font_crox3t_tn;
                break;
            case FONT_CROX4HB_TF:
                fontSel = Fonts.u8g2_font_crox4hb_tf;
                break;
            case FONT_CROX4HB_TR:
                fontSel = Fonts.u8g2_font_crox4hb_tr;
                break;
            case FONT_CROX4HB_TN:
                fontSel = Fonts.u8g2_font_crox4hb_tn;
                break;
            case FONT_CROX4H_TF:
                fontSel = Fonts.u8g2_font_crox4h_tf;
                break;
            case FONT_CROX4H_TR:
                fontSel = Fonts.u8g2_font_crox4h_tr;
                break;
            case FONT_CROX4H_TN:
                fontSel = Fonts.u8g2_font_crox4h_tn;
                break;
            case FONT_CROX4TB_TF:
                fontSel = Fonts.u8g2_font_crox4tb_tf;
                break;
            case FONT_CROX4TB_TR:
                fontSel = Fonts.u8g2_font_crox4tb_tr;
                break;
            case FONT_CROX4TB_TN:
                fontSel = Fonts.u8g2_font_crox4tb_tn;
                break;
            case FONT_CROX4T_TF:
                fontSel = Fonts.u8g2_font_crox4t_tf;
                break;
            case FONT_CROX4T_TR:
                fontSel = Fonts.u8g2_font_crox4t_tr;
                break;
            case FONT_CROX4T_TN:
                fontSel = Fonts.u8g2_font_crox4t_tn;
                break;
            case FONT_CROX5HB_TF:
                fontSel = Fonts.u8g2_font_crox5hb_tf;
                break;
            case FONT_CROX5HB_TR:
                fontSel = Fonts.u8g2_font_crox5hb_tr;
                break;
            case FONT_CROX5HB_TN:
                fontSel = Fonts.u8g2_font_crox5hb_tn;
                break;
            case FONT_CROX5H_TF:
                fontSel = Fonts.u8g2_font_crox5h_tf;
                break;
            case FONT_CROX5H_TR:
                fontSel = Fonts.u8g2_font_crox5h_tr;
                break;
            case FONT_CROX5H_TN:
                fontSel = Fonts.u8g2_font_crox5h_tn;
                break;
            case FONT_CROX5TB_TF:
                fontSel = Fonts.u8g2_font_crox5tb_tf;
                break;
            case FONT_CROX5TB_TR:
                fontSel = Fonts.u8g2_font_crox5tb_tr;
                break;
            case FONT_CROX5TB_TN:
                fontSel = Fonts.u8g2_font_crox5tb_tn;
                break;
            case FONT_CROX5T_TF:
                fontSel = Fonts.u8g2_font_crox5t_tf;
                break;
            case FONT_CROX5T_TR:
                fontSel = Fonts.u8g2_font_crox5t_tr;
                break;
            case FONT_CROX5T_TN:
                fontSel = Fonts.u8g2_font_crox5t_tn;
                break;
            case FONT_CU12_TF:
                fontSel = Fonts.u8g2_font_cu12_tf;
                break;
            case FONT_CU12_TR:
                fontSel = Fonts.u8g2_font_cu12_tr;
                break;
            case FONT_CU12_TN:
                fontSel = Fonts.u8g2_font_cu12_tn;
                break;
            case FONT_CU12_TE:
                fontSel = Fonts.u8g2_font_cu12_te;
                break;
            case FONT_CU12_HF:
                fontSel = Fonts.u8g2_font_cu12_hf;
                break;
            case FONT_CU12_HR:
                fontSel = Fonts.u8g2_font_cu12_hr;
                break;
            case FONT_CU12_HN:
                fontSel = Fonts.u8g2_font_cu12_hn;
                break;
            case FONT_CU12_HE:
                fontSel = Fonts.u8g2_font_cu12_he;
                break;
            case FONT_CU12_MF:
                fontSel = Fonts.u8g2_font_cu12_mf;
                break;
            case FONT_CU12_MR:
                fontSel = Fonts.u8g2_font_cu12_mr;
                break;
            case FONT_CU12_MN:
                fontSel = Fonts.u8g2_font_cu12_mn;
                break;
            case FONT_CU12_ME:
                fontSel = Fonts.u8g2_font_cu12_me;
                break;
            case FONT_CU12_T_SYMBOLS:
                fontSel = Fonts.u8g2_font_cu12_t_symbols;
                break;
            case FONT_CU12_H_SYMBOLS:
                fontSel = Fonts.u8g2_font_cu12_h_symbols;
                break;
            case FONT_CU12_T_GREEK:
                fontSel = Fonts.u8g2_font_cu12_t_greek;
                break;
            case FONT_CU12_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_cu12_t_cyrillic;
                break;
            case FONT_CU12_T_TIBETAN:
                fontSel = Fonts.u8g2_font_cu12_t_tibetan;
                break;
            case FONT_CU12_T_HEBREW:
                fontSel = Fonts.u8g2_font_cu12_t_hebrew;
                break;
            case FONT_CU12_T_ARABIC:
                fontSel = Fonts.u8g2_font_cu12_t_arabic;
                break;
            case FONT_UNIFONT_TF:
                fontSel = Fonts.u8g2_font_unifont_tf;
                break;
            case FONT_UNIFONT_TR:
                fontSel = Fonts.u8g2_font_unifont_tr;
                break;
            case FONT_UNIFONT_TE:
                fontSel = Fonts.u8g2_font_unifont_te;
                break;
            case FONT_UNIFONT_T_LATIN:
                fontSel = Fonts.u8g2_font_unifont_t_latin;
                break;
            case FONT_UNIFONT_T_EXTENDED:
                fontSel = Fonts.u8g2_font_unifont_t_extended;
                break;
            case FONT_UNIFONT_T_72_73:
                fontSel = Fonts.u8g2_font_unifont_t_72_73;
                break;
            case FONT_UNIFONT_T_0_72_73:
                fontSel = Fonts.u8g2_font_unifont_t_0_72_73;
                break;
            case FONT_UNIFONT_T_75:
                fontSel = Fonts.u8g2_font_unifont_t_75;
                break;
            case FONT_UNIFONT_T_0_75:
                fontSel = Fonts.u8g2_font_unifont_t_0_75;
                break;
            case FONT_UNIFONT_T_76:
                fontSel = Fonts.u8g2_font_unifont_t_76;
                break;
            case FONT_UNIFONT_T_0_76:
                fontSel = Fonts.u8g2_font_unifont_t_0_76;
                break;
            case FONT_UNIFONT_T_77:
                fontSel = Fonts.u8g2_font_unifont_t_77;
                break;
            case FONT_UNIFONT_T_0_77:
                fontSel = Fonts.u8g2_font_unifont_t_0_77;
                break;
            case FONT_UNIFONT_T_78_79:
                fontSel = Fonts.u8g2_font_unifont_t_78_79;
                break;
            case FONT_UNIFONT_T_0_78_79:
                fontSel = Fonts.u8g2_font_unifont_t_0_78_79;
                break;
            case FONT_UNIFONT_T_86:
                fontSel = Fonts.u8g2_font_unifont_t_86;
                break;
            case FONT_UNIFONT_T_0_86:
                fontSel = Fonts.u8g2_font_unifont_t_0_86;
                break;
            case FONT_UNIFONT_T_GREEK:
                fontSel = Fonts.u8g2_font_unifont_t_greek;
                break;
            case FONT_UNIFONT_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_unifont_t_cyrillic;
                break;
            case FONT_UNIFONT_T_HEBREW:
                fontSel = Fonts.u8g2_font_unifont_t_hebrew;
                break;
            case FONT_UNIFONT_T_BENGALI:
                fontSel = Fonts.u8g2_font_unifont_t_bengali;
                break;
            case FONT_UNIFONT_T_TIBETAN:
                fontSel = Fonts.u8g2_font_unifont_t_tibetan;
                break;
            case FONT_UNIFONT_T_URDU:
                fontSel = Fonts.u8g2_font_unifont_t_urdu;
                break;
            case FONT_UNIFONT_T_POLISH:
                fontSel = Fonts.u8g2_font_unifont_t_polish;
                break;
            case FONT_UNIFONT_T_DEVANAGARI:
                fontSel = Fonts.u8g2_font_unifont_t_devanagari;
                break;
            case FONT_UNIFONT_T_ARABIC:
                fontSel = Fonts.u8g2_font_unifont_t_arabic;
                break;
            case FONT_UNIFONT_T_SYMBOLS:
                fontSel = Fonts.u8g2_font_unifont_t_symbols;
                break;
            case FONT_UNIFONT_H_SYMBOLS:
                fontSel = Fonts.u8g2_font_unifont_h_symbols;
                break;
            case FONT_UNIFONT_T_EMOTICONS:
                fontSel = Fonts.u8g2_font_unifont_t_emoticons;
                break;
            case FONT_UNIFONT_T_ANIMALS:
                fontSel = Fonts.u8g2_font_unifont_t_animals;
                break;
            case FONT_UNIFONT_T_DOMINO:
                fontSel = Fonts.u8g2_font_unifont_t_domino;
                break;
            case FONT_UNIFONT_T_CARDS:
                fontSel = Fonts.u8g2_font_unifont_t_cards;
                break;
            case FONT_UNIFONT_T_WEATHER:
                fontSel = Fonts.u8g2_font_unifont_t_weather;
                break;
            case FONT_UNIFONT_T_CHINESE1:
                fontSel = Fonts.u8g2_font_unifont_t_chinese1;
                break;
            case FONT_UNIFONT_T_CHINESE2:
                fontSel = Fonts.u8g2_font_unifont_t_chinese2;
                break;
            case FONT_UNIFONT_T_CHINESE3:
                fontSel = Fonts.u8g2_font_unifont_t_chinese3;
                break;
            case FONT_UNIFONT_T_JAPANESE1:
                fontSel = Fonts.u8g2_font_unifont_t_japanese1;
                break;
            case FONT_UNIFONT_T_JAPANESE2:
                fontSel = Fonts.u8g2_font_unifont_t_japanese2;
                break;
            case FONT_UNIFONT_T_JAPANESE3:
                fontSel = Fonts.u8g2_font_unifont_t_japanese3;
                break;
            case FONT_UNIFONT_T_KOREAN1:
                fontSel = Fonts.u8g2_font_unifont_t_korean1;
                break;
            case FONT_UNIFONT_T_KOREAN2:
                fontSel = Fonts.u8g2_font_unifont_t_korean2;
                break;
            case FONT_UNIFONT_T_VIETNAMESE1:
                fontSel = Fonts.u8g2_font_unifont_t_vietnamese1;
                break;
            case FONT_UNIFONT_T_VIETNAMESE2:
                fontSel = Fonts.u8g2_font_unifont_t_vietnamese2;
                break;
            case FONT_GB16ST_T_1:
                fontSel = Fonts.u8g2_font_gb16st_t_1;
                break;
            case FONT_GB16ST_T_2:
                fontSel = Fonts.u8g2_font_gb16st_t_2;
                break;
            case FONT_GB16ST_T_3:
                fontSel = Fonts.u8g2_font_gb16st_t_3;
                break;
            case FONT_GB24ST_T_1:
                fontSel = Fonts.u8g2_font_gb24st_t_1;
                break;
            case FONT_GB24ST_T_2:
                fontSel = Fonts.u8g2_font_gb24st_t_2;
                break;
            case FONT_GB24ST_T_3:
                fontSel = Fonts.u8g2_font_gb24st_t_3;
                break;
            case FONT_WQY12_T_CHINESE1:
                fontSel = Fonts.u8g2_font_wqy12_t_chinese1;
                break;
            case FONT_WQY12_T_CHINESE2:
                fontSel = Fonts.u8g2_font_wqy12_t_chinese2;
                break;
            case FONT_WQY12_T_CHINESE3:
                fontSel = Fonts.u8g2_font_wqy12_t_chinese3;
                break;
            case FONT_WQY12_T_GB2312:
                fontSel = Fonts.u8g2_font_wqy12_t_gb2312;
                break;
            case FONT_WQY12_T_GB2312A:
                fontSel = Fonts.u8g2_font_wqy12_t_gb2312a;
                break;
            case FONT_WQY12_T_GB2312B:
                fontSel = Fonts.u8g2_font_wqy12_t_gb2312b;
                break;
            case FONT_WQY13_T_CHINESE1:
                fontSel = Fonts.u8g2_font_wqy13_t_chinese1;
                break;
            case FONT_WQY13_T_CHINESE2:
                fontSel = Fonts.u8g2_font_wqy13_t_chinese2;
                break;
            case FONT_WQY13_T_CHINESE3:
                fontSel = Fonts.u8g2_font_wqy13_t_chinese3;
                break;
            case FONT_WQY13_T_GB2312:
                fontSel = Fonts.u8g2_font_wqy13_t_gb2312;
                break;
            case FONT_WQY13_T_GB2312A:
                fontSel = Fonts.u8g2_font_wqy13_t_gb2312a;
                break;
            case FONT_WQY13_T_GB2312B:
                fontSel = Fonts.u8g2_font_wqy13_t_gb2312b;
                break;
            case FONT_WQY14_T_CHINESE1:
                fontSel = Fonts.u8g2_font_wqy14_t_chinese1;
                break;
            case FONT_WQY14_T_CHINESE2:
                fontSel = Fonts.u8g2_font_wqy14_t_chinese2;
                break;
            case FONT_WQY14_T_CHINESE3:
                fontSel = Fonts.u8g2_font_wqy14_t_chinese3;
                break;
            case FONT_WQY14_T_GB2312:
                fontSel = Fonts.u8g2_font_wqy14_t_gb2312;
                break;
            case FONT_WQY14_T_GB2312A:
                fontSel = Fonts.u8g2_font_wqy14_t_gb2312a;
                break;
            case FONT_WQY14_T_GB2312B:
                fontSel = Fonts.u8g2_font_wqy14_t_gb2312b;
                break;
            case FONT_WQY15_T_CHINESE1:
                fontSel = Fonts.u8g2_font_wqy15_t_chinese1;
                break;
            case FONT_WQY15_T_CHINESE2:
                fontSel = Fonts.u8g2_font_wqy15_t_chinese2;
                break;
            case FONT_WQY15_T_CHINESE3:
                fontSel = Fonts.u8g2_font_wqy15_t_chinese3;
                break;
            case FONT_WQY15_T_GB2312:
                fontSel = Fonts.u8g2_font_wqy15_t_gb2312;
                break;
            case FONT_WQY15_T_GB2312A:
                fontSel = Fonts.u8g2_font_wqy15_t_gb2312a;
                break;
            case FONT_WQY15_T_GB2312B:
                fontSel = Fonts.u8g2_font_wqy15_t_gb2312b;
                break;
            case FONT_WQY16_T_CHINESE1:
                fontSel = Fonts.u8g2_font_wqy16_t_chinese1;
                break;
            case FONT_WQY16_T_CHINESE2:
                fontSel = Fonts.u8g2_font_wqy16_t_chinese2;
                break;
            case FONT_WQY16_T_CHINESE3:
                fontSel = Fonts.u8g2_font_wqy16_t_chinese3;
                break;
            case FONT_WQY16_T_GB2312:
                fontSel = Fonts.u8g2_font_wqy16_t_gb2312;
                break;
            case FONT_WQY16_T_GB2312A:
                fontSel = Fonts.u8g2_font_wqy16_t_gb2312a;
                break;
            case FONT_WQY16_T_GB2312B:
                fontSel = Fonts.u8g2_font_wqy16_t_gb2312b;
                break;
            case FONT_B10_T_JAPANESE1:
                fontSel = Fonts.u8g2_font_b10_t_japanese1;
                break;
            case FONT_B10_T_JAPANESE2:
                fontSel = Fonts.u8g2_font_b10_t_japanese2;
                break;
            case FONT_B10_B_T_JAPANESE1:
                fontSel = Fonts.u8g2_font_b10_b_t_japanese1;
                break;
            case FONT_B10_B_T_JAPANESE2:
                fontSel = Fonts.u8g2_font_b10_b_t_japanese2;
                break;
            case FONT_F10_T_JAPANESE1:
                fontSel = Fonts.u8g2_font_f10_t_japanese1;
                break;
            case FONT_F10_T_JAPANESE2:
                fontSel = Fonts.u8g2_font_f10_t_japanese2;
                break;
            case FONT_F10_B_T_JAPANESE1:
                fontSel = Fonts.u8g2_font_f10_b_t_japanese1;
                break;
            case FONT_F10_B_T_JAPANESE2:
                fontSel = Fonts.u8g2_font_f10_b_t_japanese2;
                break;
            case FONT_B12_T_JAPANESE1:
                fontSel = Fonts.u8g2_font_b12_t_japanese1;
                break;
            case FONT_B12_T_JAPANESE2:
                fontSel = Fonts.u8g2_font_b12_t_japanese2;
                break;
            case FONT_B12_T_JAPANESE3:
                fontSel = Fonts.u8g2_font_b12_t_japanese3;
                break;
            case FONT_B12_B_T_JAPANESE1:
                fontSel = Fonts.u8g2_font_b12_b_t_japanese1;
                break;
            case FONT_B12_B_T_JAPANESE2:
                fontSel = Fonts.u8g2_font_b12_b_t_japanese2;
                break;
            case FONT_B12_B_T_JAPANESE3:
                fontSel = Fonts.u8g2_font_b12_b_t_japanese3;
                break;
            case FONT_F12_T_JAPANESE1:
                fontSel = Fonts.u8g2_font_f12_t_japanese1;
                break;
            case FONT_F12_T_JAPANESE2:
                fontSel = Fonts.u8g2_font_f12_t_japanese2;
                break;
            case FONT_F12_B_T_JAPANESE1:
                fontSel = Fonts.u8g2_font_f12_b_t_japanese1;
                break;
            case FONT_F12_B_T_JAPANESE2:
                fontSel = Fonts.u8g2_font_f12_b_t_japanese2;
                break;
            case FONT_B16_T_JAPANESE1:
                fontSel = Fonts.u8g2_font_b16_t_japanese1;
                break;
            case FONT_B16_T_JAPANESE2:
                fontSel = Fonts.u8g2_font_b16_t_japanese2;
                break;
            case FONT_B16_T_JAPANESE3:
                fontSel = Fonts.u8g2_font_b16_t_japanese3;
                break;
            case FONT_B16_B_T_JAPANESE1:
                fontSel = Fonts.u8g2_font_b16_b_t_japanese1;
                break;
            case FONT_B16_B_T_JAPANESE2:
                fontSel = Fonts.u8g2_font_b16_b_t_japanese2;
                break;
            case FONT_B16_B_T_JAPANESE3:
                fontSel = Fonts.u8g2_font_b16_b_t_japanese3;
                break;
            case FONT_F16_T_JAPANESE1:
                fontSel = Fonts.u8g2_font_f16_t_japanese1;
                break;
            case FONT_F16_T_JAPANESE2:
                fontSel = Fonts.u8g2_font_f16_t_japanese2;
                break;
            case FONT_F16_B_T_JAPANESE1:
                fontSel = Fonts.u8g2_font_f16_b_t_japanese1;
                break;
            case FONT_F16_B_T_JAPANESE2:
                fontSel = Fonts.u8g2_font_f16_b_t_japanese2;
                break;
            case FONT_ARTOSSANS8_8R:
                fontSel = Fonts.u8g2_font_artossans8_8r;
                break;
            case FONT_ARTOSSANS8_8N:
                fontSel = Fonts.u8g2_font_artossans8_8n;
                break;
            case FONT_ARTOSSANS8_8U:
                fontSel = Fonts.u8g2_font_artossans8_8u;
                break;
            case FONT_ARTOSSERIF8_8R:
                fontSel = Fonts.u8g2_font_artosserif8_8r;
                break;
            case FONT_ARTOSSERIF8_8N:
                fontSel = Fonts.u8g2_font_artosserif8_8n;
                break;
            case FONT_ARTOSSERIF8_8U:
                fontSel = Fonts.u8g2_font_artosserif8_8u;
                break;
            case FONT_CHROMA48MEDIUM8_8R:
                fontSel = Fonts.u8g2_font_chroma48medium8_8r;
                break;
            case FONT_CHROMA48MEDIUM8_8N:
                fontSel = Fonts.u8g2_font_chroma48medium8_8n;
                break;
            case FONT_CHROMA48MEDIUM8_8U:
                fontSel = Fonts.u8g2_font_chroma48medium8_8u;
                break;
            case FONT_SAIKYOSANSBOLD8_8N:
                fontSel = Fonts.u8g2_font_saikyosansbold8_8n;
                break;
            case FONT_SAIKYOSANSBOLD8_8U:
                fontSel = Fonts.u8g2_font_saikyosansbold8_8u;
                break;
            case FONT_TORUSSANSBOLD8_8R:
                fontSel = Fonts.u8g2_font_torussansbold8_8r;
                break;
            case FONT_TORUSSANSBOLD8_8N:
                fontSel = Fonts.u8g2_font_torussansbold8_8n;
                break;
            case FONT_TORUSSANSBOLD8_8U:
                fontSel = Fonts.u8g2_font_torussansbold8_8u;
                break;
            case FONT_VICTORIABOLD8_8R:
                fontSel = Fonts.u8g2_font_victoriabold8_8r;
                break;
            case FONT_VICTORIABOLD8_8N:
                fontSel = Fonts.u8g2_font_victoriabold8_8n;
                break;
            case FONT_VICTORIABOLD8_8U:
                fontSel = Fonts.u8g2_font_victoriabold8_8u;
                break;
            case FONT_VICTORIAMEDIUM8_8R:
                fontSel = Fonts.u8g2_font_victoriamedium8_8r;
                break;
            case FONT_VICTORIAMEDIUM8_8N:
                fontSel = Fonts.u8g2_font_victoriamedium8_8n;
                break;
            case FONT_VICTORIAMEDIUM8_8U:
                fontSel = Fonts.u8g2_font_victoriamedium8_8u;
                break;
            case FONT_COURB08_TF:
                fontSel = Fonts.u8g2_font_courB08_tf;
                break;
            case FONT_COURB08_TR:
                fontSel = Fonts.u8g2_font_courB08_tr;
                break;
            case FONT_COURB08_TN:
                fontSel = Fonts.u8g2_font_courB08_tn;
                break;
            case FONT_COURB10_TF:
                fontSel = Fonts.u8g2_font_courB10_tf;
                break;
            case FONT_COURB10_TR:
                fontSel = Fonts.u8g2_font_courB10_tr;
                break;
            case FONT_COURB10_TN:
                fontSel = Fonts.u8g2_font_courB10_tn;
                break;
            case FONT_COURB12_TF:
                fontSel = Fonts.u8g2_font_courB12_tf;
                break;
            case FONT_COURB12_TR:
                fontSel = Fonts.u8g2_font_courB12_tr;
                break;
            case FONT_COURB12_TN:
                fontSel = Fonts.u8g2_font_courB12_tn;
                break;
            case FONT_COURB14_TF:
                fontSel = Fonts.u8g2_font_courB14_tf;
                break;
            case FONT_COURB14_TR:
                fontSel = Fonts.u8g2_font_courB14_tr;
                break;
            case FONT_COURB14_TN:
                fontSel = Fonts.u8g2_font_courB14_tn;
                break;
            case FONT_COURB18_TF:
                fontSel = Fonts.u8g2_font_courB18_tf;
                break;
            case FONT_COURB18_TR:
                fontSel = Fonts.u8g2_font_courB18_tr;
                break;
            case FONT_COURB18_TN:
                fontSel = Fonts.u8g2_font_courB18_tn;
                break;
            case FONT_COURB24_TF:
                fontSel = Fonts.u8g2_font_courB24_tf;
                break;
            case FONT_COURB24_TR:
                fontSel = Fonts.u8g2_font_courB24_tr;
                break;
            case FONT_COURB24_TN:
                fontSel = Fonts.u8g2_font_courB24_tn;
                break;
            case FONT_COURR08_TF:
                fontSel = Fonts.u8g2_font_courR08_tf;
                break;
            case FONT_COURR08_TR:
                fontSel = Fonts.u8g2_font_courR08_tr;
                break;
            case FONT_COURR08_TN:
                fontSel = Fonts.u8g2_font_courR08_tn;
                break;
            case FONT_COURR10_TF:
                fontSel = Fonts.u8g2_font_courR10_tf;
                break;
            case FONT_COURR10_TR:
                fontSel = Fonts.u8g2_font_courR10_tr;
                break;
            case FONT_COURR10_TN:
                fontSel = Fonts.u8g2_font_courR10_tn;
                break;
            case FONT_COURR12_TF:
                fontSel = Fonts.u8g2_font_courR12_tf;
                break;
            case FONT_COURR12_TR:
                fontSel = Fonts.u8g2_font_courR12_tr;
                break;
            case FONT_COURR12_TN:
                fontSel = Fonts.u8g2_font_courR12_tn;
                break;
            case FONT_COURR14_TF:
                fontSel = Fonts.u8g2_font_courR14_tf;
                break;
            case FONT_COURR14_TR:
                fontSel = Fonts.u8g2_font_courR14_tr;
                break;
            case FONT_COURR14_TN:
                fontSel = Fonts.u8g2_font_courR14_tn;
                break;
            case FONT_COURR18_TF:
                fontSel = Fonts.u8g2_font_courR18_tf;
                break;
            case FONT_COURR18_TR:
                fontSel = Fonts.u8g2_font_courR18_tr;
                break;
            case FONT_COURR18_TN:
                fontSel = Fonts.u8g2_font_courR18_tn;
                break;
            case FONT_COURR24_TF:
                fontSel = Fonts.u8g2_font_courR24_tf;
                break;
            case FONT_COURR24_TR:
                fontSel = Fonts.u8g2_font_courR24_tr;
                break;
            case FONT_COURR24_TN:
                fontSel = Fonts.u8g2_font_courR24_tn;
                break;
            case FONT_HELVB08_TF:
                fontSel = Fonts.u8g2_font_helvB08_tf;
                break;
            case FONT_HELVB08_TR:
                fontSel = Fonts.u8g2_font_helvB08_tr;
                break;
            case FONT_HELVB08_TN:
                fontSel = Fonts.u8g2_font_helvB08_tn;
                break;
            case FONT_HELVB08_TE:
                fontSel = Fonts.u8g2_font_helvB08_te;
                break;
            case FONT_HELVB10_TF:
                fontSel = Fonts.u8g2_font_helvB10_tf;
                break;
            case FONT_HELVB10_TR:
                fontSel = Fonts.u8g2_font_helvB10_tr;
                break;
            case FONT_HELVB10_TN:
                fontSel = Fonts.u8g2_font_helvB10_tn;
                break;
            case FONT_HELVB10_TE:
                fontSel = Fonts.u8g2_font_helvB10_te;
                break;
            case FONT_HELVB12_TF:
                fontSel = Fonts.u8g2_font_helvB12_tf;
                break;
            case FONT_HELVB12_TR:
                fontSel = Fonts.u8g2_font_helvB12_tr;
                break;
            case FONT_HELVB12_TN:
                fontSel = Fonts.u8g2_font_helvB12_tn;
                break;
            case FONT_HELVB12_TE:
                fontSel = Fonts.u8g2_font_helvB12_te;
                break;
            case FONT_HELVB14_TF:
                fontSel = Fonts.u8g2_font_helvB14_tf;
                break;
            case FONT_HELVB14_TR:
                fontSel = Fonts.u8g2_font_helvB14_tr;
                break;
            case FONT_HELVB14_TN:
                fontSel = Fonts.u8g2_font_helvB14_tn;
                break;
            case FONT_HELVB14_TE:
                fontSel = Fonts.u8g2_font_helvB14_te;
                break;
            case FONT_HELVB18_TF:
                fontSel = Fonts.u8g2_font_helvB18_tf;
                break;
            case FONT_HELVB18_TR:
                fontSel = Fonts.u8g2_font_helvB18_tr;
                break;
            case FONT_HELVB18_TN:
                fontSel = Fonts.u8g2_font_helvB18_tn;
                break;
            case FONT_HELVB18_TE:
                fontSel = Fonts.u8g2_font_helvB18_te;
                break;
            case FONT_HELVB24_TF:
                fontSel = Fonts.u8g2_font_helvB24_tf;
                break;
            case FONT_HELVB24_TR:
                fontSel = Fonts.u8g2_font_helvB24_tr;
                break;
            case FONT_HELVB24_TN:
                fontSel = Fonts.u8g2_font_helvB24_tn;
                break;
            case FONT_HELVB24_TE:
                fontSel = Fonts.u8g2_font_helvB24_te;
                break;
            case FONT_HELVR08_TF:
                fontSel = Fonts.u8g2_font_helvR08_tf;
                break;
            case FONT_HELVR08_TR:
                fontSel = Fonts.u8g2_font_helvR08_tr;
                break;
            case FONT_HELVR08_TN:
                fontSel = Fonts.u8g2_font_helvR08_tn;
                break;
            case FONT_HELVR08_TE:
                fontSel = Fonts.u8g2_font_helvR08_te;
                break;
            case FONT_HELVR10_TF:
                fontSel = Fonts.u8g2_font_helvR10_tf;
                break;
            case FONT_HELVR10_TR:
                fontSel = Fonts.u8g2_font_helvR10_tr;
                break;
            case FONT_HELVR10_TN:
                fontSel = Fonts.u8g2_font_helvR10_tn;
                break;
            case FONT_HELVR10_TE:
                fontSel = Fonts.u8g2_font_helvR10_te;
                break;
            case FONT_HELVR12_TF:
                fontSel = Fonts.u8g2_font_helvR12_tf;
                break;
            case FONT_HELVR12_TR:
                fontSel = Fonts.u8g2_font_helvR12_tr;
                break;
            case FONT_HELVR12_TN:
                fontSel = Fonts.u8g2_font_helvR12_tn;
                break;
            case FONT_HELVR12_TE:
                fontSel = Fonts.u8g2_font_helvR12_te;
                break;
            case FONT_HELVR14_TF:
                fontSel = Fonts.u8g2_font_helvR14_tf;
                break;
            case FONT_HELVR14_TR:
                fontSel = Fonts.u8g2_font_helvR14_tr;
                break;
            case FONT_HELVR14_TN:
                fontSel = Fonts.u8g2_font_helvR14_tn;
                break;
            case FONT_HELVR14_TE:
                fontSel = Fonts.u8g2_font_helvR14_te;
                break;
            case FONT_HELVR18_TF:
                fontSel = Fonts.u8g2_font_helvR18_tf;
                break;
            case FONT_HELVR18_TR:
                fontSel = Fonts.u8g2_font_helvR18_tr;
                break;
            case FONT_HELVR18_TN:
                fontSel = Fonts.u8g2_font_helvR18_tn;
                break;
            case FONT_HELVR18_TE:
                fontSel = Fonts.u8g2_font_helvR18_te;
                break;
            case FONT_HELVR24_TF:
                fontSel = Fonts.u8g2_font_helvR24_tf;
                break;
            case FONT_HELVR24_TR:
                fontSel = Fonts.u8g2_font_helvR24_tr;
                break;
            case FONT_HELVR24_TN:
                fontSel = Fonts.u8g2_font_helvR24_tn;
                break;
            case FONT_HELVR24_TE:
                fontSel = Fonts.u8g2_font_helvR24_te;
                break;
            case FONT_NCENB08_TF:
                fontSel = Fonts.u8g2_font_ncenB08_tf;
                break;
            case FONT_NCENB08_TR:
                fontSel = Fonts.u8g2_font_ncenB08_tr;
                break;
            case FONT_NCENB08_TN:
                fontSel = Fonts.u8g2_font_ncenB08_tn;
                break;
            case FONT_NCENB08_TE:
                fontSel = Fonts.u8g2_font_ncenB08_te;
                break;
            case FONT_NCENB10_TF:
                fontSel = Fonts.u8g2_font_ncenB10_tf;
                break;
            case FONT_NCENB10_TR:
                fontSel = Fonts.u8g2_font_ncenB10_tr;
                break;
            case FONT_NCENB10_TN:
                fontSel = Fonts.u8g2_font_ncenB10_tn;
                break;
            case FONT_NCENB10_TE:
                fontSel = Fonts.u8g2_font_ncenB10_te;
                break;
            case FONT_NCENB12_TF:
                fontSel = Fonts.u8g2_font_ncenB12_tf;
                break;
            case FONT_NCENB12_TR:
                fontSel = Fonts.u8g2_font_ncenB12_tr;
                break;
            case FONT_NCENB12_TN:
                fontSel = Fonts.u8g2_font_ncenB12_tn;
                break;
            case FONT_NCENB12_TE:
                fontSel = Fonts.u8g2_font_ncenB12_te;
                break;
            case FONT_NCENB14_TF:
                fontSel = Fonts.u8g2_font_ncenB14_tf;
                break;
            case FONT_NCENB14_TR:
                fontSel = Fonts.u8g2_font_ncenB14_tr;
                break;
            case FONT_NCENB14_TN:
                fontSel = Fonts.u8g2_font_ncenB14_tn;
                break;
            case FONT_NCENB14_TE:
                fontSel = Fonts.u8g2_font_ncenB14_te;
                break;
            case FONT_NCENB18_TF:
                fontSel = Fonts.u8g2_font_ncenB18_tf;
                break;
            case FONT_NCENB18_TR:
                fontSel = Fonts.u8g2_font_ncenB18_tr;
                break;
            case FONT_NCENB18_TN:
                fontSel = Fonts.u8g2_font_ncenB18_tn;
                break;
            case FONT_NCENB18_TE:
                fontSel = Fonts.u8g2_font_ncenB18_te;
                break;
            case FONT_NCENB24_TF:
                fontSel = Fonts.u8g2_font_ncenB24_tf;
                break;
            case FONT_NCENB24_TR:
                fontSel = Fonts.u8g2_font_ncenB24_tr;
                break;
            case FONT_NCENB24_TN:
                fontSel = Fonts.u8g2_font_ncenB24_tn;
                break;
            case FONT_NCENB24_TE:
                fontSel = Fonts.u8g2_font_ncenB24_te;
                break;
            case FONT_NCENR08_TF:
                fontSel = Fonts.u8g2_font_ncenR08_tf;
                break;
            case FONT_NCENR08_TR:
                fontSel = Fonts.u8g2_font_ncenR08_tr;
                break;
            case FONT_NCENR08_TN:
                fontSel = Fonts.u8g2_font_ncenR08_tn;
                break;
            case FONT_NCENR08_TE:
                fontSel = Fonts.u8g2_font_ncenR08_te;
                break;
            case FONT_NCENR10_TF:
                fontSel = Fonts.u8g2_font_ncenR10_tf;
                break;
            case FONT_NCENR10_TR:
                fontSel = Fonts.u8g2_font_ncenR10_tr;
                break;
            case FONT_NCENR10_TN:
                fontSel = Fonts.u8g2_font_ncenR10_tn;
                break;
            case FONT_NCENR10_TE:
                fontSel = Fonts.u8g2_font_ncenR10_te;
                break;
            case FONT_NCENR12_TF:
                fontSel = Fonts.u8g2_font_ncenR12_tf;
                break;
            case FONT_NCENR12_TR:
                fontSel = Fonts.u8g2_font_ncenR12_tr;
                break;
            case FONT_NCENR12_TN:
                fontSel = Fonts.u8g2_font_ncenR12_tn;
                break;
            case FONT_NCENR12_TE:
                fontSel = Fonts.u8g2_font_ncenR12_te;
                break;
            case FONT_NCENR14_TF:
                fontSel = Fonts.u8g2_font_ncenR14_tf;
                break;
            case FONT_NCENR14_TR:
                fontSel = Fonts.u8g2_font_ncenR14_tr;
                break;
            case FONT_NCENR14_TN:
                fontSel = Fonts.u8g2_font_ncenR14_tn;
                break;
            case FONT_NCENR14_TE:
                fontSel = Fonts.u8g2_font_ncenR14_te;
                break;
            case FONT_NCENR18_TF:
                fontSel = Fonts.u8g2_font_ncenR18_tf;
                break;
            case FONT_NCENR18_TR:
                fontSel = Fonts.u8g2_font_ncenR18_tr;
                break;
            case FONT_NCENR18_TN:
                fontSel = Fonts.u8g2_font_ncenR18_tn;
                break;
            case FONT_NCENR18_TE:
                fontSel = Fonts.u8g2_font_ncenR18_te;
                break;
            case FONT_NCENR24_TF:
                fontSel = Fonts.u8g2_font_ncenR24_tf;
                break;
            case FONT_NCENR24_TR:
                fontSel = Fonts.u8g2_font_ncenR24_tr;
                break;
            case FONT_NCENR24_TN:
                fontSel = Fonts.u8g2_font_ncenR24_tn;
                break;
            case FONT_NCENR24_TE:
                fontSel = Fonts.u8g2_font_ncenR24_te;
                break;
            case FONT_TIMB08_TF:
                fontSel = Fonts.u8g2_font_timB08_tf;
                break;
            case FONT_TIMB08_TR:
                fontSel = Fonts.u8g2_font_timB08_tr;
                break;
            case FONT_TIMB08_TN:
                fontSel = Fonts.u8g2_font_timB08_tn;
                break;
            case FONT_TIMB10_TF:
                fontSel = Fonts.u8g2_font_timB10_tf;
                break;
            case FONT_TIMB10_TR:
                fontSel = Fonts.u8g2_font_timB10_tr;
                break;
            case FONT_TIMB10_TN:
                fontSel = Fonts.u8g2_font_timB10_tn;
                break;
            case FONT_TIMB12_TF:
                fontSel = Fonts.u8g2_font_timB12_tf;
                break;
            case FONT_TIMB12_TR:
                fontSel = Fonts.u8g2_font_timB12_tr;
                break;
            case FONT_TIMB12_TN:
                fontSel = Fonts.u8g2_font_timB12_tn;
                break;
            case FONT_TIMB14_TF:
                fontSel = Fonts.u8g2_font_timB14_tf;
                break;
            case FONT_TIMB14_TR:
                fontSel = Fonts.u8g2_font_timB14_tr;
                break;
            case FONT_TIMB14_TN:
                fontSel = Fonts.u8g2_font_timB14_tn;
                break;
            case FONT_TIMB18_TF:
                fontSel = Fonts.u8g2_font_timB18_tf;
                break;
            case FONT_TIMB18_TR:
                fontSel = Fonts.u8g2_font_timB18_tr;
                break;
            case FONT_TIMB18_TN:
                fontSel = Fonts.u8g2_font_timB18_tn;
                break;
            case FONT_TIMB24_TF:
                fontSel = Fonts.u8g2_font_timB24_tf;
                break;
            case FONT_TIMB24_TR:
                fontSel = Fonts.u8g2_font_timB24_tr;
                break;
            case FONT_TIMB24_TN:
                fontSel = Fonts.u8g2_font_timB24_tn;
                break;
            case FONT_TIMR08_TF:
                fontSel = Fonts.u8g2_font_timR08_tf;
                break;
            case FONT_TIMR08_TR:
                fontSel = Fonts.u8g2_font_timR08_tr;
                break;
            case FONT_TIMR08_TN:
                fontSel = Fonts.u8g2_font_timR08_tn;
                break;
            case FONT_TIMR10_TF:
                fontSel = Fonts.u8g2_font_timR10_tf;
                break;
            case FONT_TIMR10_TR:
                fontSel = Fonts.u8g2_font_timR10_tr;
                break;
            case FONT_TIMR10_TN:
                fontSel = Fonts.u8g2_font_timR10_tn;
                break;
            case FONT_TIMR12_TF:
                fontSel = Fonts.u8g2_font_timR12_tf;
                break;
            case FONT_TIMR12_TR:
                fontSel = Fonts.u8g2_font_timR12_tr;
                break;
            case FONT_TIMR12_TN:
                fontSel = Fonts.u8g2_font_timR12_tn;
                break;
            case FONT_TIMR14_TF:
                fontSel = Fonts.u8g2_font_timR14_tf;
                break;
            case FONT_TIMR14_TR:
                fontSel = Fonts.u8g2_font_timR14_tr;
                break;
            case FONT_TIMR14_TN:
                fontSel = Fonts.u8g2_font_timR14_tn;
                break;
            case FONT_TIMR18_TF:
                fontSel = Fonts.u8g2_font_timR18_tf;
                break;
            case FONT_TIMR18_TR:
                fontSel = Fonts.u8g2_font_timR18_tr;
                break;
            case FONT_TIMR18_TN:
                fontSel = Fonts.u8g2_font_timR18_tn;
                break;
            case FONT_TIMR24_TF:
                fontSel = Fonts.u8g2_font_timR24_tf;
                break;
            case FONT_TIMR24_TR:
                fontSel = Fonts.u8g2_font_timR24_tr;
                break;
            case FONT_TIMR24_TN:
                fontSel = Fonts.u8g2_font_timR24_tn;
                break;
            case FONT_LUBB08_TF:
                fontSel = Fonts.u8g2_font_lubB08_tf;
                break;
            case FONT_LUBB08_TR:
                fontSel = Fonts.u8g2_font_lubB08_tr;
                break;
            case FONT_LUBB08_TN:
                fontSel = Fonts.u8g2_font_lubB08_tn;
                break;
            case FONT_LUBB08_TE:
                fontSel = Fonts.u8g2_font_lubB08_te;
                break;
            case FONT_LUBB10_TF:
                fontSel = Fonts.u8g2_font_lubB10_tf;
                break;
            case FONT_LUBB10_TR:
                fontSel = Fonts.u8g2_font_lubB10_tr;
                break;
            case FONT_LUBB10_TN:
                fontSel = Fonts.u8g2_font_lubB10_tn;
                break;
            case FONT_LUBB10_TE:
                fontSel = Fonts.u8g2_font_lubB10_te;
                break;
            case FONT_LUBB12_TF:
                fontSel = Fonts.u8g2_font_lubB12_tf;
                break;
            case FONT_LUBB12_TR:
                fontSel = Fonts.u8g2_font_lubB12_tr;
                break;
            case FONT_LUBB12_TN:
                fontSel = Fonts.u8g2_font_lubB12_tn;
                break;
            case FONT_LUBB12_TE:
                fontSel = Fonts.u8g2_font_lubB12_te;
                break;
            case FONT_LUBB14_TF:
                fontSel = Fonts.u8g2_font_lubB14_tf;
                break;
            case FONT_LUBB14_TR:
                fontSel = Fonts.u8g2_font_lubB14_tr;
                break;
            case FONT_LUBB14_TN:
                fontSel = Fonts.u8g2_font_lubB14_tn;
                break;
            case FONT_LUBB14_TE:
                fontSel = Fonts.u8g2_font_lubB14_te;
                break;
            case FONT_LUBB18_TF:
                fontSel = Fonts.u8g2_font_lubB18_tf;
                break;
            case FONT_LUBB18_TR:
                fontSel = Fonts.u8g2_font_lubB18_tr;
                break;
            case FONT_LUBB18_TN:
                fontSel = Fonts.u8g2_font_lubB18_tn;
                break;
            case FONT_LUBB18_TE:
                fontSel = Fonts.u8g2_font_lubB18_te;
                break;
            case FONT_LUBB19_TF:
                fontSel = Fonts.u8g2_font_lubB19_tf;
                break;
            case FONT_LUBB19_TR:
                fontSel = Fonts.u8g2_font_lubB19_tr;
                break;
            case FONT_LUBB19_TN:
                fontSel = Fonts.u8g2_font_lubB19_tn;
                break;
            case FONT_LUBB19_TE:
                fontSel = Fonts.u8g2_font_lubB19_te;
                break;
            case FONT_LUBB24_TF:
                fontSel = Fonts.u8g2_font_lubB24_tf;
                break;
            case FONT_LUBB24_TR:
                fontSel = Fonts.u8g2_font_lubB24_tr;
                break;
            case FONT_LUBB24_TN:
                fontSel = Fonts.u8g2_font_lubB24_tn;
                break;
            case FONT_LUBB24_TE:
                fontSel = Fonts.u8g2_font_lubB24_te;
                break;
            case FONT_LUBBI08_TF:
                fontSel = Fonts.u8g2_font_lubBI08_tf;
                break;
            case FONT_LUBBI08_TR:
                fontSel = Fonts.u8g2_font_lubBI08_tr;
                break;
            case FONT_LUBBI08_TN:
                fontSel = Fonts.u8g2_font_lubBI08_tn;
                break;
            case FONT_LUBBI08_TE:
                fontSel = Fonts.u8g2_font_lubBI08_te;
                break;
            case FONT_LUBBI10_TF:
                fontSel = Fonts.u8g2_font_lubBI10_tf;
                break;
            case FONT_LUBBI10_TR:
                fontSel = Fonts.u8g2_font_lubBI10_tr;
                break;
            case FONT_LUBBI10_TN:
                fontSel = Fonts.u8g2_font_lubBI10_tn;
                break;
            case FONT_LUBBI10_TE:
                fontSel = Fonts.u8g2_font_lubBI10_te;
                break;
            case FONT_LUBBI12_TF:
                fontSel = Fonts.u8g2_font_lubBI12_tf;
                break;
            case FONT_LUBBI12_TR:
                fontSel = Fonts.u8g2_font_lubBI12_tr;
                break;
            case FONT_LUBBI12_TN:
                fontSel = Fonts.u8g2_font_lubBI12_tn;
                break;
            case FONT_LUBBI12_TE:
                fontSel = Fonts.u8g2_font_lubBI12_te;
                break;
            case FONT_LUBBI14_TF:
                fontSel = Fonts.u8g2_font_lubBI14_tf;
                break;
            case FONT_LUBBI14_TR:
                fontSel = Fonts.u8g2_font_lubBI14_tr;
                break;
            case FONT_LUBBI14_TN:
                fontSel = Fonts.u8g2_font_lubBI14_tn;
                break;
            case FONT_LUBBI14_TE:
                fontSel = Fonts.u8g2_font_lubBI14_te;
                break;
            case FONT_LUBBI18_TF:
                fontSel = Fonts.u8g2_font_lubBI18_tf;
                break;
            case FONT_LUBBI18_TR:
                fontSel = Fonts.u8g2_font_lubBI18_tr;
                break;
            case FONT_LUBBI18_TN:
                fontSel = Fonts.u8g2_font_lubBI18_tn;
                break;
            case FONT_LUBBI18_TE:
                fontSel = Fonts.u8g2_font_lubBI18_te;
                break;
            case FONT_LUBBI19_TF:
                fontSel = Fonts.u8g2_font_lubBI19_tf;
                break;
            case FONT_LUBBI19_TR:
                fontSel = Fonts.u8g2_font_lubBI19_tr;
                break;
            case FONT_LUBBI19_TN:
                fontSel = Fonts.u8g2_font_lubBI19_tn;
                break;
            case FONT_LUBBI19_TE:
                fontSel = Fonts.u8g2_font_lubBI19_te;
                break;
            case FONT_LUBBI24_TF:
                fontSel = Fonts.u8g2_font_lubBI24_tf;
                break;
            case FONT_LUBBI24_TR:
                fontSel = Fonts.u8g2_font_lubBI24_tr;
                break;
            case FONT_LUBBI24_TN:
                fontSel = Fonts.u8g2_font_lubBI24_tn;
                break;
            case FONT_LUBBI24_TE:
                fontSel = Fonts.u8g2_font_lubBI24_te;
                break;
            case FONT_LUBI08_TF:
                fontSel = Fonts.u8g2_font_lubI08_tf;
                break;
            case FONT_LUBI08_TR:
                fontSel = Fonts.u8g2_font_lubI08_tr;
                break;
            case FONT_LUBI08_TN:
                fontSel = Fonts.u8g2_font_lubI08_tn;
                break;
            case FONT_LUBI08_TE:
                fontSel = Fonts.u8g2_font_lubI08_te;
                break;
            case FONT_LUBI10_TF:
                fontSel = Fonts.u8g2_font_lubI10_tf;
                break;
            case FONT_LUBI10_TR:
                fontSel = Fonts.u8g2_font_lubI10_tr;
                break;
            case FONT_LUBI10_TN:
                fontSel = Fonts.u8g2_font_lubI10_tn;
                break;
            case FONT_LUBI10_TE:
                fontSel = Fonts.u8g2_font_lubI10_te;
                break;
            case FONT_LUBI12_TF:
                fontSel = Fonts.u8g2_font_lubI12_tf;
                break;
            case FONT_LUBI12_TR:
                fontSel = Fonts.u8g2_font_lubI12_tr;
                break;
            case FONT_LUBI12_TN:
                fontSel = Fonts.u8g2_font_lubI12_tn;
                break;
            case FONT_LUBI12_TE:
                fontSel = Fonts.u8g2_font_lubI12_te;
                break;
            case FONT_LUBI14_TF:
                fontSel = Fonts.u8g2_font_lubI14_tf;
                break;
            case FONT_LUBI14_TR:
                fontSel = Fonts.u8g2_font_lubI14_tr;
                break;
            case FONT_LUBI14_TN:
                fontSel = Fonts.u8g2_font_lubI14_tn;
                break;
            case FONT_LUBI14_TE:
                fontSel = Fonts.u8g2_font_lubI14_te;
                break;
            case FONT_LUBI18_TF:
                fontSel = Fonts.u8g2_font_lubI18_tf;
                break;
            case FONT_LUBI18_TR:
                fontSel = Fonts.u8g2_font_lubI18_tr;
                break;
            case FONT_LUBI18_TN:
                fontSel = Fonts.u8g2_font_lubI18_tn;
                break;
            case FONT_LUBI18_TE:
                fontSel = Fonts.u8g2_font_lubI18_te;
                break;
            case FONT_LUBI19_TF:
                fontSel = Fonts.u8g2_font_lubI19_tf;
                break;
            case FONT_LUBI19_TR:
                fontSel = Fonts.u8g2_font_lubI19_tr;
                break;
            case FONT_LUBI19_TN:
                fontSel = Fonts.u8g2_font_lubI19_tn;
                break;
            case FONT_LUBI19_TE:
                fontSel = Fonts.u8g2_font_lubI19_te;
                break;
            case FONT_LUBI24_TF:
                fontSel = Fonts.u8g2_font_lubI24_tf;
                break;
            case FONT_LUBI24_TR:
                fontSel = Fonts.u8g2_font_lubI24_tr;
                break;
            case FONT_LUBI24_TN:
                fontSel = Fonts.u8g2_font_lubI24_tn;
                break;
            case FONT_LUBI24_TE:
                fontSel = Fonts.u8g2_font_lubI24_te;
                break;
            case FONT_LUBIS08_TF:
                fontSel = Fonts.u8g2_font_luBIS08_tf;
                break;
            case FONT_LUBIS08_TR:
                fontSel = Fonts.u8g2_font_luBIS08_tr;
                break;
            case FONT_LUBIS08_TN:
                fontSel = Fonts.u8g2_font_luBIS08_tn;
                break;
            case FONT_LUBIS08_TE:
                fontSel = Fonts.u8g2_font_luBIS08_te;
                break;
            case FONT_LUBIS10_TF:
                fontSel = Fonts.u8g2_font_luBIS10_tf;
                break;
            case FONT_LUBIS10_TR:
                fontSel = Fonts.u8g2_font_luBIS10_tr;
                break;
            case FONT_LUBIS10_TN:
                fontSel = Fonts.u8g2_font_luBIS10_tn;
                break;
            case FONT_LUBIS10_TE:
                fontSel = Fonts.u8g2_font_luBIS10_te;
                break;
            case FONT_LUBIS12_TF:
                fontSel = Fonts.u8g2_font_luBIS12_tf;
                break;
            case FONT_LUBIS12_TR:
                fontSel = Fonts.u8g2_font_luBIS12_tr;
                break;
            case FONT_LUBIS12_TN:
                fontSel = Fonts.u8g2_font_luBIS12_tn;
                break;
            case FONT_LUBIS12_TE:
                fontSel = Fonts.u8g2_font_luBIS12_te;
                break;
            case FONT_LUBIS14_TF:
                fontSel = Fonts.u8g2_font_luBIS14_tf;
                break;
            case FONT_LUBIS14_TR:
                fontSel = Fonts.u8g2_font_luBIS14_tr;
                break;
            case FONT_LUBIS14_TN:
                fontSel = Fonts.u8g2_font_luBIS14_tn;
                break;
            case FONT_LUBIS14_TE:
                fontSel = Fonts.u8g2_font_luBIS14_te;
                break;
            case FONT_LUBIS18_TF:
                fontSel = Fonts.u8g2_font_luBIS18_tf;
                break;
            case FONT_LUBIS18_TR:
                fontSel = Fonts.u8g2_font_luBIS18_tr;
                break;
            case FONT_LUBIS18_TN:
                fontSel = Fonts.u8g2_font_luBIS18_tn;
                break;
            case FONT_LUBIS18_TE:
                fontSel = Fonts.u8g2_font_luBIS18_te;
                break;
            case FONT_LUBIS19_TF:
                fontSel = Fonts.u8g2_font_luBIS19_tf;
                break;
            case FONT_LUBIS19_TR:
                fontSel = Fonts.u8g2_font_luBIS19_tr;
                break;
            case FONT_LUBIS19_TN:
                fontSel = Fonts.u8g2_font_luBIS19_tn;
                break;
            case FONT_LUBIS19_TE:
                fontSel = Fonts.u8g2_font_luBIS19_te;
                break;
            case FONT_LUBIS24_TF:
                fontSel = Fonts.u8g2_font_luBIS24_tf;
                break;
            case FONT_LUBIS24_TR:
                fontSel = Fonts.u8g2_font_luBIS24_tr;
                break;
            case FONT_LUBIS24_TN:
                fontSel = Fonts.u8g2_font_luBIS24_tn;
                break;
            case FONT_LUBIS24_TE:
                fontSel = Fonts.u8g2_font_luBIS24_te;
                break;
            case FONT_LUBR08_TF:
                fontSel = Fonts.u8g2_font_lubR08_tf;
                break;
            case FONT_LUBR08_TR:
                fontSel = Fonts.u8g2_font_lubR08_tr;
                break;
            case FONT_LUBR08_TN:
                fontSel = Fonts.u8g2_font_lubR08_tn;
                break;
            case FONT_LUBR08_TE:
                fontSel = Fonts.u8g2_font_lubR08_te;
                break;
            case FONT_LUBR10_TF:
                fontSel = Fonts.u8g2_font_lubR10_tf;
                break;
            case FONT_LUBR10_TR:
                fontSel = Fonts.u8g2_font_lubR10_tr;
                break;
            case FONT_LUBR10_TN:
                fontSel = Fonts.u8g2_font_lubR10_tn;
                break;
            case FONT_LUBR10_TE:
                fontSel = Fonts.u8g2_font_lubR10_te;
                break;
            case FONT_LUBR12_TF:
                fontSel = Fonts.u8g2_font_lubR12_tf;
                break;
            case FONT_LUBR12_TR:
                fontSel = Fonts.u8g2_font_lubR12_tr;
                break;
            case FONT_LUBR12_TN:
                fontSel = Fonts.u8g2_font_lubR12_tn;
                break;
            case FONT_LUBR12_TE:
                fontSel = Fonts.u8g2_font_lubR12_te;
                break;
            case FONT_LUBR14_TF:
                fontSel = Fonts.u8g2_font_lubR14_tf;
                break;
            case FONT_LUBR14_TR:
                fontSel = Fonts.u8g2_font_lubR14_tr;
                break;
            case FONT_LUBR14_TN:
                fontSel = Fonts.u8g2_font_lubR14_tn;
                break;
            case FONT_LUBR14_TE:
                fontSel = Fonts.u8g2_font_lubR14_te;
                break;
            case FONT_LUBR18_TF:
                fontSel = Fonts.u8g2_font_lubR18_tf;
                break;
            case FONT_LUBR18_TR:
                fontSel = Fonts.u8g2_font_lubR18_tr;
                break;
            case FONT_LUBR18_TN:
                fontSel = Fonts.u8g2_font_lubR18_tn;
                break;
            case FONT_LUBR18_TE:
                fontSel = Fonts.u8g2_font_lubR18_te;
                break;
            case FONT_LUBR19_TF:
                fontSel = Fonts.u8g2_font_lubR19_tf;
                break;
            case FONT_LUBR19_TR:
                fontSel = Fonts.u8g2_font_lubR19_tr;
                break;
            case FONT_LUBR19_TN:
                fontSel = Fonts.u8g2_font_lubR19_tn;
                break;
            case FONT_LUBR19_TE:
                fontSel = Fonts.u8g2_font_lubR19_te;
                break;
            case FONT_LUBR24_TF:
                fontSel = Fonts.u8g2_font_lubR24_tf;
                break;
            case FONT_LUBR24_TR:
                fontSel = Fonts.u8g2_font_lubR24_tr;
                break;
            case FONT_LUBR24_TN:
                fontSel = Fonts.u8g2_font_lubR24_tn;
                break;
            case FONT_LUBR24_TE:
                fontSel = Fonts.u8g2_font_lubR24_te;
                break;
            case FONT_LUBS08_TF:
                fontSel = Fonts.u8g2_font_luBS08_tf;
                break;
            case FONT_LUBS08_TR:
                fontSel = Fonts.u8g2_font_luBS08_tr;
                break;
            case FONT_LUBS08_TN:
                fontSel = Fonts.u8g2_font_luBS08_tn;
                break;
            case FONT_LUBS08_TE:
                fontSel = Fonts.u8g2_font_luBS08_te;
                break;
            case FONT_LUBS10_TF:
                fontSel = Fonts.u8g2_font_luBS10_tf;
                break;
            case FONT_LUBS10_TR:
                fontSel = Fonts.u8g2_font_luBS10_tr;
                break;
            case FONT_LUBS10_TN:
                fontSel = Fonts.u8g2_font_luBS10_tn;
                break;
            case FONT_LUBS10_TE:
                fontSel = Fonts.u8g2_font_luBS10_te;
                break;
            case FONT_LUBS12_TF:
                fontSel = Fonts.u8g2_font_luBS12_tf;
                break;
            case FONT_LUBS12_TR:
                fontSel = Fonts.u8g2_font_luBS12_tr;
                break;
            case FONT_LUBS12_TN:
                fontSel = Fonts.u8g2_font_luBS12_tn;
                break;
            case FONT_LUBS12_TE:
                fontSel = Fonts.u8g2_font_luBS12_te;
                break;
            case FONT_LUBS14_TF:
                fontSel = Fonts.u8g2_font_luBS14_tf;
                break;
            case FONT_LUBS14_TR:
                fontSel = Fonts.u8g2_font_luBS14_tr;
                break;
            case FONT_LUBS14_TN:
                fontSel = Fonts.u8g2_font_luBS14_tn;
                break;
            case FONT_LUBS14_TE:
                fontSel = Fonts.u8g2_font_luBS14_te;
                break;
            case FONT_LUBS18_TF:
                fontSel = Fonts.u8g2_font_luBS18_tf;
                break;
            case FONT_LUBS18_TR:
                fontSel = Fonts.u8g2_font_luBS18_tr;
                break;
            case FONT_LUBS18_TN:
                fontSel = Fonts.u8g2_font_luBS18_tn;
                break;
            case FONT_LUBS18_TE:
                fontSel = Fonts.u8g2_font_luBS18_te;
                break;
            case FONT_LUBS19_TF:
                fontSel = Fonts.u8g2_font_luBS19_tf;
                break;
            case FONT_LUBS19_TR:
                fontSel = Fonts.u8g2_font_luBS19_tr;
                break;
            case FONT_LUBS19_TN:
                fontSel = Fonts.u8g2_font_luBS19_tn;
                break;
            case FONT_LUBS19_TE:
                fontSel = Fonts.u8g2_font_luBS19_te;
                break;
            case FONT_LUBS24_TF:
                fontSel = Fonts.u8g2_font_luBS24_tf;
                break;
            case FONT_LUBS24_TR:
                fontSel = Fonts.u8g2_font_luBS24_tr;
                break;
            case FONT_LUBS24_TN:
                fontSel = Fonts.u8g2_font_luBS24_tn;
                break;
            case FONT_LUBS24_TE:
                fontSel = Fonts.u8g2_font_luBS24_te;
                break;
            case FONT_LUIS08_TF:
                fontSel = Fonts.u8g2_font_luIS08_tf;
                break;
            case FONT_LUIS08_TR:
                fontSel = Fonts.u8g2_font_luIS08_tr;
                break;
            case FONT_LUIS08_TN:
                fontSel = Fonts.u8g2_font_luIS08_tn;
                break;
            case FONT_LUIS08_TE:
                fontSel = Fonts.u8g2_font_luIS08_te;
                break;
            case FONT_LUIS10_TF:
                fontSel = Fonts.u8g2_font_luIS10_tf;
                break;
            case FONT_LUIS10_TR:
                fontSel = Fonts.u8g2_font_luIS10_tr;
                break;
            case FONT_LUIS10_TN:
                fontSel = Fonts.u8g2_font_luIS10_tn;
                break;
            case FONT_LUIS10_TE:
                fontSel = Fonts.u8g2_font_luIS10_te;
                break;
            case FONT_LUIS12_TF:
                fontSel = Fonts.u8g2_font_luIS12_tf;
                break;
            case FONT_LUIS12_TR:
                fontSel = Fonts.u8g2_font_luIS12_tr;
                break;
            case FONT_LUIS12_TN:
                fontSel = Fonts.u8g2_font_luIS12_tn;
                break;
            case FONT_LUIS12_TE:
                fontSel = Fonts.u8g2_font_luIS12_te;
                break;
            case FONT_LUIS14_TF:
                fontSel = Fonts.u8g2_font_luIS14_tf;
                break;
            case FONT_LUIS14_TR:
                fontSel = Fonts.u8g2_font_luIS14_tr;
                break;
            case FONT_LUIS14_TN:
                fontSel = Fonts.u8g2_font_luIS14_tn;
                break;
            case FONT_LUIS14_TE:
                fontSel = Fonts.u8g2_font_luIS14_te;
                break;
            case FONT_LUIS18_TF:
                fontSel = Fonts.u8g2_font_luIS18_tf;
                break;
            case FONT_LUIS18_TR:
                fontSel = Fonts.u8g2_font_luIS18_tr;
                break;
            case FONT_LUIS18_TN:
                fontSel = Fonts.u8g2_font_luIS18_tn;
                break;
            case FONT_LUIS18_TE:
                fontSel = Fonts.u8g2_font_luIS18_te;
                break;
            case FONT_LUIS19_TF:
                fontSel = Fonts.u8g2_font_luIS19_tf;
                break;
            case FONT_LUIS19_TR:
                fontSel = Fonts.u8g2_font_luIS19_tr;
                break;
            case FONT_LUIS19_TN:
                fontSel = Fonts.u8g2_font_luIS19_tn;
                break;
            case FONT_LUIS19_TE:
                fontSel = Fonts.u8g2_font_luIS19_te;
                break;
            case FONT_LUIS24_TF:
                fontSel = Fonts.u8g2_font_luIS24_tf;
                break;
            case FONT_LUIS24_TR:
                fontSel = Fonts.u8g2_font_luIS24_tr;
                break;
            case FONT_LUIS24_TN:
                fontSel = Fonts.u8g2_font_luIS24_tn;
                break;
            case FONT_LUIS24_TE:
                fontSel = Fonts.u8g2_font_luIS24_te;
                break;
            case FONT_LURS08_TF:
                fontSel = Fonts.u8g2_font_luRS08_tf;
                break;
            case FONT_LURS08_TR:
                fontSel = Fonts.u8g2_font_luRS08_tr;
                break;
            case FONT_LURS08_TN:
                fontSel = Fonts.u8g2_font_luRS08_tn;
                break;
            case FONT_LURS08_TE:
                fontSel = Fonts.u8g2_font_luRS08_te;
                break;
            case FONT_LURS10_TF:
                fontSel = Fonts.u8g2_font_luRS10_tf;
                break;
            case FONT_LURS10_TR:
                fontSel = Fonts.u8g2_font_luRS10_tr;
                break;
            case FONT_LURS10_TN:
                fontSel = Fonts.u8g2_font_luRS10_tn;
                break;
            case FONT_LURS10_TE:
                fontSel = Fonts.u8g2_font_luRS10_te;
                break;
            case FONT_LURS12_TF:
                fontSel = Fonts.u8g2_font_luRS12_tf;
                break;
            case FONT_LURS12_TR:
                fontSel = Fonts.u8g2_font_luRS12_tr;
                break;
            case FONT_LURS12_TN:
                fontSel = Fonts.u8g2_font_luRS12_tn;
                break;
            case FONT_LURS12_TE:
                fontSel = Fonts.u8g2_font_luRS12_te;
                break;
            case FONT_LURS14_TF:
                fontSel = Fonts.u8g2_font_luRS14_tf;
                break;
            case FONT_LURS14_TR:
                fontSel = Fonts.u8g2_font_luRS14_tr;
                break;
            case FONT_LURS14_TN:
                fontSel = Fonts.u8g2_font_luRS14_tn;
                break;
            case FONT_LURS14_TE:
                fontSel = Fonts.u8g2_font_luRS14_te;
                break;
            case FONT_LURS18_TF:
                fontSel = Fonts.u8g2_font_luRS18_tf;
                break;
            case FONT_LURS18_TR:
                fontSel = Fonts.u8g2_font_luRS18_tr;
                break;
            case FONT_LURS18_TN:
                fontSel = Fonts.u8g2_font_luRS18_tn;
                break;
            case FONT_LURS18_TE:
                fontSel = Fonts.u8g2_font_luRS18_te;
                break;
            case FONT_LURS19_TF:
                fontSel = Fonts.u8g2_font_luRS19_tf;
                break;
            case FONT_LURS19_TR:
                fontSel = Fonts.u8g2_font_luRS19_tr;
                break;
            case FONT_LURS19_TN:
                fontSel = Fonts.u8g2_font_luRS19_tn;
                break;
            case FONT_LURS19_TE:
                fontSel = Fonts.u8g2_font_luRS19_te;
                break;
            case FONT_LURS24_TF:
                fontSel = Fonts.u8g2_font_luRS24_tf;
                break;
            case FONT_LURS24_TR:
                fontSel = Fonts.u8g2_font_luRS24_tr;
                break;
            case FONT_LURS24_TN:
                fontSel = Fonts.u8g2_font_luRS24_tn;
                break;
            case FONT_LURS24_TE:
                fontSel = Fonts.u8g2_font_luRS24_te;
                break;
            case FONT_BABY_TF:
                fontSel = Fonts.u8g2_font_baby_tf;
                break;
            case FONT_BABY_TR:
                fontSel = Fonts.u8g2_font_baby_tr;
                break;
            case FONT_BABY_TN:
                fontSel = Fonts.u8g2_font_baby_tn;
                break;
            case FONT_BLIPFEST_07_TR:
                fontSel = Fonts.u8g2_font_blipfest_07_tr;
                break;
            case FONT_BLIPFEST_07_TN:
                fontSel = Fonts.u8g2_font_blipfest_07_tn;
                break;
            case FONT_CHIKITA_TF:
                fontSel = Fonts.u8g2_font_chikita_tf;
                break;
            case FONT_CHIKITA_TR:
                fontSel = Fonts.u8g2_font_chikita_tr;
                break;
            case FONT_CHIKITA_TN:
                fontSel = Fonts.u8g2_font_chikita_tn;
                break;
            case FONT_LUCASFONT_ALTERNATE_TF:
                fontSel = Fonts.u8g2_font_lucasfont_alternate_tf;
                break;
            case FONT_LUCASFONT_ALTERNATE_TR:
                fontSel = Fonts.u8g2_font_lucasfont_alternate_tr;
                break;
            case FONT_LUCASFONT_ALTERNATE_TN:
                fontSel = Fonts.u8g2_font_lucasfont_alternate_tn;
                break;
            case FONT_P01TYPE_TF:
                fontSel = Fonts.u8g2_font_p01type_tf;
                break;
            case FONT_P01TYPE_TR:
                fontSel = Fonts.u8g2_font_p01type_tr;
                break;
            case FONT_P01TYPE_TN:
                fontSel = Fonts.u8g2_font_p01type_tn;
                break;
            case FONT_PIXELLE_MICRO_TR:
                fontSel = Fonts.u8g2_font_pixelle_micro_tr;
                break;
            case FONT_PIXELLE_MICRO_TN:
                fontSel = Fonts.u8g2_font_pixelle_micro_tn;
                break;
            case FONT_ROBOT_DE_NIRO_TF:
                fontSel = Fonts.u8g2_font_robot_de_niro_tf;
                break;
            case FONT_ROBOT_DE_NIRO_TR:
                fontSel = Fonts.u8g2_font_robot_de_niro_tr;
                break;
            case FONT_ROBOT_DE_NIRO_TN:
                fontSel = Fonts.u8g2_font_robot_de_niro_tn;
                break;
            case FONT_TRIXEL_SQUARE_TF:
                fontSel = Fonts.u8g2_font_trixel_square_tf;
                break;
            case FONT_TRIXEL_SQUARE_TR:
                fontSel = Fonts.u8g2_font_trixel_square_tr;
                break;
            case FONT_TRIXEL_SQUARE_TN:
                fontSel = Fonts.u8g2_font_trixel_square_tn;
                break;
            case FONT_HAXRCORP4089_TR:
                fontSel = Fonts.u8g2_font_haxrcorp4089_tr;
                break;
            case FONT_HAXRCORP4089_TN:
                fontSel = Fonts.u8g2_font_haxrcorp4089_tn;
                break;
            case FONT_HAXRCORP4089_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_haxrcorp4089_t_cyrillic;
                break;
            case FONT_BUBBLE_TR:
                fontSel = Fonts.u8g2_font_bubble_tr;
                break;
            case FONT_BUBBLE_TN:
                fontSel = Fonts.u8g2_font_bubble_tn;
                break;
            case FONT_CARDIMON_PIXEL_TF:
                fontSel = Fonts.u8g2_font_cardimon_pixel_tf;
                break;
            case FONT_CARDIMON_PIXEL_TR:
                fontSel = Fonts.u8g2_font_cardimon_pixel_tr;
                break;
            case FONT_CARDIMON_PIXEL_TN:
                fontSel = Fonts.u8g2_font_cardimon_pixel_tn;
                break;
            case FONT_MANIAC_TF:
                fontSel = Fonts.u8g2_font_maniac_tf;
                break;
            case FONT_MANIAC_TR:
                fontSel = Fonts.u8g2_font_maniac_tr;
                break;
            case FONT_MANIAC_TN:
                fontSel = Fonts.u8g2_font_maniac_tn;
                break;
            case FONT_MANIAC_TE:
                fontSel = Fonts.u8g2_font_maniac_te;
                break;
            case FONT_LUCASARTS_SCUMM_SUBTITLE_O_TF:
                fontSel = Fonts.u8g2_font_lucasarts_scumm_subtitle_o_tf;
                break;
            case FONT_LUCASARTS_SCUMM_SUBTITLE_O_TR:
                fontSel = Fonts.u8g2_font_lucasarts_scumm_subtitle_o_tr;
                break;
            case FONT_LUCASARTS_SCUMM_SUBTITLE_O_TN:
                fontSel = Fonts.u8g2_font_lucasarts_scumm_subtitle_o_tn;
                break;
            case FONT_LUCASARTS_SCUMM_SUBTITLE_R_TF:
                fontSel = Fonts.u8g2_font_lucasarts_scumm_subtitle_r_tf;
                break;
            case FONT_LUCASARTS_SCUMM_SUBTITLE_R_TR:
                fontSel = Fonts.u8g2_font_lucasarts_scumm_subtitle_r_tr;
                break;
            case FONT_LUCASARTS_SCUMM_SUBTITLE_R_TN:
                fontSel = Fonts.u8g2_font_lucasarts_scumm_subtitle_r_tn;
                break;
            case FONT_UTOPIA24_TF:
                fontSel = Fonts.u8g2_font_utopia24_tf;
                break;
            case FONT_UTOPIA24_TR:
                fontSel = Fonts.u8g2_font_utopia24_tr;
                break;
            case FONT_UTOPIA24_TN:
                fontSel = Fonts.u8g2_font_utopia24_tn;
                break;
            case FONT_UTOPIA24_TE:
                fontSel = Fonts.u8g2_font_utopia24_te;
                break;
            case FONT_M_C_KIDS_NES_CREDITS_FONT_TR:
                fontSel = Fonts.u8g2_font_m_c_kids_nes_credits_font_tr;
                break;
            case FONT_CHARGEN_92_TF:
                fontSel = Fonts.u8g2_font_chargen_92_tf;
                break;
            case FONT_CHARGEN_92_TR:
                fontSel = Fonts.u8g2_font_chargen_92_tr;
                break;
            case FONT_CHARGEN_92_TN:
                fontSel = Fonts.u8g2_font_chargen_92_tn;
                break;
            case FONT_CHARGEN_92_TE:
                fontSel = Fonts.u8g2_font_chargen_92_te;
                break;
            case FONT_CHARGEN_92_MF:
                fontSel = Fonts.u8g2_font_chargen_92_mf;
                break;
            case FONT_CHARGEN_92_MR:
                fontSel = Fonts.u8g2_font_chargen_92_mr;
                break;
            case FONT_CHARGEN_92_MN:
                fontSel = Fonts.u8g2_font_chargen_92_mn;
                break;
            case FONT_CHARGEN_92_ME:
                fontSel = Fonts.u8g2_font_chargen_92_me;
                break;
            case FONT_FUB11_TF:
                fontSel = Fonts.u8g2_font_fub11_tf;
                break;
            case FONT_FUB11_TR:
                fontSel = Fonts.u8g2_font_fub11_tr;
                break;
            case FONT_FUB11_TN:
                fontSel = Fonts.u8g2_font_fub11_tn;
                break;
            case FONT_FUB14_TF:
                fontSel = Fonts.u8g2_font_fub14_tf;
                break;
            case FONT_FUB14_TR:
                fontSel = Fonts.u8g2_font_fub14_tr;
                break;
            case FONT_FUB14_TN:
                fontSel = Fonts.u8g2_font_fub14_tn;
                break;
            case FONT_FUB17_TF:
                fontSel = Fonts.u8g2_font_fub17_tf;
                break;
            case FONT_FUB17_TR:
                fontSel = Fonts.u8g2_font_fub17_tr;
                break;
            case FONT_FUB17_TN:
                fontSel = Fonts.u8g2_font_fub17_tn;
                break;
            case FONT_FUB20_TF:
                fontSel = Fonts.u8g2_font_fub20_tf;
                break;
            case FONT_FUB20_TR:
                fontSel = Fonts.u8g2_font_fub20_tr;
                break;
            case FONT_FUB20_TN:
                fontSel = Fonts.u8g2_font_fub20_tn;
                break;
            case FONT_FUB25_TF:
                fontSel = Fonts.u8g2_font_fub25_tf;
                break;
            case FONT_FUB25_TR:
                fontSel = Fonts.u8g2_font_fub25_tr;
                break;
            case FONT_FUB25_TN:
                fontSel = Fonts.u8g2_font_fub25_tn;
                break;
            case FONT_FUB30_TF:
                fontSel = Fonts.u8g2_font_fub30_tf;
                break;
            case FONT_FUB30_TR:
                fontSel = Fonts.u8g2_font_fub30_tr;
                break;
            case FONT_FUB30_TN:
                fontSel = Fonts.u8g2_font_fub30_tn;
                break;
            case FONT_FUB35_TF:
                fontSel = Fonts.u8g2_font_fub35_tf;
                break;
            case FONT_FUB35_TR:
                fontSel = Fonts.u8g2_font_fub35_tr;
                break;
            case FONT_FUB35_TN:
                fontSel = Fonts.u8g2_font_fub35_tn;
                break;
            case FONT_FUB42_TF:
                fontSel = Fonts.u8g2_font_fub42_tf;
                break;
            case FONT_FUB42_TR:
                fontSel = Fonts.u8g2_font_fub42_tr;
                break;
            case FONT_FUB42_TN:
                fontSel = Fonts.u8g2_font_fub42_tn;
                break;
            case FONT_FUB49_TN:
                fontSel = Fonts.u8g2_font_fub49_tn;
                break;
            case FONT_FUB11_T_SYMBOL:
                fontSel = Fonts.u8g2_font_fub11_t_symbol;
                break;
            case FONT_FUB14_T_SYMBOL:
                fontSel = Fonts.u8g2_font_fub14_t_symbol;
                break;
            case FONT_FUB17_T_SYMBOL:
                fontSel = Fonts.u8g2_font_fub17_t_symbol;
                break;
            case FONT_FUB20_T_SYMBOL:
                fontSel = Fonts.u8g2_font_fub20_t_symbol;
                break;
            case FONT_FUB25_T_SYMBOL:
                fontSel = Fonts.u8g2_font_fub25_t_symbol;
                break;
            case FONT_FUB30_T_SYMBOL:
                fontSel = Fonts.u8g2_font_fub30_t_symbol;
                break;
            case FONT_FUB35_T_SYMBOL:
                fontSel = Fonts.u8g2_font_fub35_t_symbol;
                break;
            case FONT_FUB42_T_SYMBOL:
                fontSel = Fonts.u8g2_font_fub42_t_symbol;
                break;
            case FONT_FUB49_T_SYMBOL:
                fontSel = Fonts.u8g2_font_fub49_t_symbol;
                break;
            case FONT_FUR11_TF:
                fontSel = Fonts.u8g2_font_fur11_tf;
                break;
            case FONT_FUR11_TR:
                fontSel = Fonts.u8g2_font_fur11_tr;
                break;
            case FONT_FUR11_TN:
                fontSel = Fonts.u8g2_font_fur11_tn;
                break;
            case FONT_FUR14_TF:
                fontSel = Fonts.u8g2_font_fur14_tf;
                break;
            case FONT_FUR14_TR:
                fontSel = Fonts.u8g2_font_fur14_tr;
                break;
            case FONT_FUR14_TN:
                fontSel = Fonts.u8g2_font_fur14_tn;
                break;
            case FONT_FUR17_TF:
                fontSel = Fonts.u8g2_font_fur17_tf;
                break;
            case FONT_FUR17_TR:
                fontSel = Fonts.u8g2_font_fur17_tr;
                break;
            case FONT_FUR17_TN:
                fontSel = Fonts.u8g2_font_fur17_tn;
                break;
            case FONT_FUR20_TF:
                fontSel = Fonts.u8g2_font_fur20_tf;
                break;
            case FONT_FUR20_TR:
                fontSel = Fonts.u8g2_font_fur20_tr;
                break;
            case FONT_FUR20_TN:
                fontSel = Fonts.u8g2_font_fur20_tn;
                break;
            case FONT_FUR25_TF:
                fontSel = Fonts.u8g2_font_fur25_tf;
                break;
            case FONT_FUR25_TR:
                fontSel = Fonts.u8g2_font_fur25_tr;
                break;
            case FONT_FUR25_TN:
                fontSel = Fonts.u8g2_font_fur25_tn;
                break;
            case FONT_FUR30_TF:
                fontSel = Fonts.u8g2_font_fur30_tf;
                break;
            case FONT_FUR30_TR:
                fontSel = Fonts.u8g2_font_fur30_tr;
                break;
            case FONT_FUR30_TN:
                fontSel = Fonts.u8g2_font_fur30_tn;
                break;
            case FONT_FUR35_TF:
                fontSel = Fonts.u8g2_font_fur35_tf;
                break;
            case FONT_FUR35_TR:
                fontSel = Fonts.u8g2_font_fur35_tr;
                break;
            case FONT_FUR35_TN:
                fontSel = Fonts.u8g2_font_fur35_tn;
                break;
            case FONT_FUR42_TF:
                fontSel = Fonts.u8g2_font_fur42_tf;
                break;
            case FONT_FUR42_TR:
                fontSel = Fonts.u8g2_font_fur42_tr;
                break;
            case FONT_FUR42_TN:
                fontSel = Fonts.u8g2_font_fur42_tn;
                break;
            case FONT_FUR49_TN:
                fontSel = Fonts.u8g2_font_fur49_tn;
                break;
            case FONT_FUR11_T_SYMBOL:
                fontSel = Fonts.u8g2_font_fur11_t_symbol;
                break;
            case FONT_FUR14_T_SYMBOL:
                fontSel = Fonts.u8g2_font_fur14_t_symbol;
                break;
            case FONT_FUR17_T_SYMBOL:
                fontSel = Fonts.u8g2_font_fur17_t_symbol;
                break;
            case FONT_FUR20_T_SYMBOL:
                fontSel = Fonts.u8g2_font_fur20_t_symbol;
                break;
            case FONT_FUR25_T_SYMBOL:
                fontSel = Fonts.u8g2_font_fur25_t_symbol;
                break;
            case FONT_FUR30_T_SYMBOL:
                fontSel = Fonts.u8g2_font_fur30_t_symbol;
                break;
            case FONT_FUR35_T_SYMBOL:
                fontSel = Fonts.u8g2_font_fur35_t_symbol;
                break;
            case FONT_FUR42_T_SYMBOL:
                fontSel = Fonts.u8g2_font_fur42_t_symbol;
                break;
            case FONT_FUR49_T_SYMBOL:
                fontSel = Fonts.u8g2_font_fur49_t_symbol;
                break;
            case FONT_OSB18_TF:
                fontSel = Fonts.u8g2_font_osb18_tf;
                break;
            case FONT_OSB18_TR:
                fontSel = Fonts.u8g2_font_osb18_tr;
                break;
            case FONT_OSB18_TN:
                fontSel = Fonts.u8g2_font_osb18_tn;
                break;
            case FONT_OSB21_TF:
                fontSel = Fonts.u8g2_font_osb21_tf;
                break;
            case FONT_OSB21_TR:
                fontSel = Fonts.u8g2_font_osb21_tr;
                break;
            case FONT_OSB21_TN:
                fontSel = Fonts.u8g2_font_osb21_tn;
                break;
            case FONT_OSB26_TF:
                fontSel = Fonts.u8g2_font_osb26_tf;
                break;
            case FONT_OSB26_TR:
                fontSel = Fonts.u8g2_font_osb26_tr;
                break;
            case FONT_OSB26_TN:
                fontSel = Fonts.u8g2_font_osb26_tn;
                break;
            case FONT_OSB29_TF:
                fontSel = Fonts.u8g2_font_osb29_tf;
                break;
            case FONT_OSB29_TR:
                fontSel = Fonts.u8g2_font_osb29_tr;
                break;
            case FONT_OSB29_TN:
                fontSel = Fonts.u8g2_font_osb29_tn;
                break;
            case FONT_OSB35_TF:
                fontSel = Fonts.u8g2_font_osb35_tf;
                break;
            case FONT_OSB35_TR:
                fontSel = Fonts.u8g2_font_osb35_tr;
                break;
            case FONT_OSB35_TN:
                fontSel = Fonts.u8g2_font_osb35_tn;
                break;
            case FONT_OSB41_TF:
                fontSel = Fonts.u8g2_font_osb41_tf;
                break;
            case FONT_OSB41_TR:
                fontSel = Fonts.u8g2_font_osb41_tr;
                break;
            case FONT_OSB41_TN:
                fontSel = Fonts.u8g2_font_osb41_tn;
                break;
            case FONT_OSR18_TF:
                fontSel = Fonts.u8g2_font_osr18_tf;
                break;
            case FONT_OSR18_TR:
                fontSel = Fonts.u8g2_font_osr18_tr;
                break;
            case FONT_OSR18_TN:
                fontSel = Fonts.u8g2_font_osr18_tn;
                break;
            case FONT_OSR21_TF:
                fontSel = Fonts.u8g2_font_osr21_tf;
                break;
            case FONT_OSR21_TR:
                fontSel = Fonts.u8g2_font_osr21_tr;
                break;
            case FONT_OSR21_TN:
                fontSel = Fonts.u8g2_font_osr21_tn;
                break;
            case FONT_OSR26_TF:
                fontSel = Fonts.u8g2_font_osr26_tf;
                break;
            case FONT_OSR26_TR:
                fontSel = Fonts.u8g2_font_osr26_tr;
                break;
            case FONT_OSR26_TN:
                fontSel = Fonts.u8g2_font_osr26_tn;
                break;
            case FONT_OSR29_TF:
                fontSel = Fonts.u8g2_font_osr29_tf;
                break;
            case FONT_OSR29_TR:
                fontSel = Fonts.u8g2_font_osr29_tr;
                break;
            case FONT_OSR29_TN:
                fontSel = Fonts.u8g2_font_osr29_tn;
                break;
            case FONT_OSR35_TF:
                fontSel = Fonts.u8g2_font_osr35_tf;
                break;
            case FONT_OSR35_TR:
                fontSel = Fonts.u8g2_font_osr35_tr;
                break;
            case FONT_OSR35_TN:
                fontSel = Fonts.u8g2_font_osr35_tn;
                break;
            case FONT_OSR41_TF:
                fontSel = Fonts.u8g2_font_osr41_tf;
                break;
            case FONT_OSR41_TR:
                fontSel = Fonts.u8g2_font_osr41_tr;
                break;
            case FONT_OSR41_TN:
                fontSel = Fonts.u8g2_font_osr41_tn;
                break;
            case FONT_INR16_MF:
                fontSel = Fonts.u8g2_font_inr16_mf;
                break;
            case FONT_INR16_MR:
                fontSel = Fonts.u8g2_font_inr16_mr;
                break;
            case FONT_INR16_MN:
                fontSel = Fonts.u8g2_font_inr16_mn;
                break;
            case FONT_INR19_MF:
                fontSel = Fonts.u8g2_font_inr19_mf;
                break;
            case FONT_INR19_MR:
                fontSel = Fonts.u8g2_font_inr19_mr;
                break;
            case FONT_INR19_MN:
                fontSel = Fonts.u8g2_font_inr19_mn;
                break;
            case FONT_INR21_MF:
                fontSel = Fonts.u8g2_font_inr21_mf;
                break;
            case FONT_INR21_MR:
                fontSel = Fonts.u8g2_font_inr21_mr;
                break;
            case FONT_INR21_MN:
                fontSel = Fonts.u8g2_font_inr21_mn;
                break;
            case FONT_INR24_MF:
                fontSel = Fonts.u8g2_font_inr24_mf;
                break;
            case FONT_INR24_MR:
                fontSel = Fonts.u8g2_font_inr24_mr;
                break;
            case FONT_INR24_MN:
                fontSel = Fonts.u8g2_font_inr24_mn;
                break;
            case FONT_INR24_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_inr24_t_cyrillic;
                break;
            case FONT_INR27_MF:
                fontSel = Fonts.u8g2_font_inr27_mf;
                break;
            case FONT_INR27_MR:
                fontSel = Fonts.u8g2_font_inr27_mr;
                break;
            case FONT_INR27_MN:
                fontSel = Fonts.u8g2_font_inr27_mn;
                break;
            case FONT_INR27_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_inr27_t_cyrillic;
                break;
            case FONT_INR30_MF:
                fontSel = Fonts.u8g2_font_inr30_mf;
                break;
            case FONT_INR30_MR:
                fontSel = Fonts.u8g2_font_inr30_mr;
                break;
            case FONT_INR30_MN:
                fontSel = Fonts.u8g2_font_inr30_mn;
                break;
            case FONT_INR30_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_inr30_t_cyrillic;
                break;
            case FONT_INR33_MF:
                fontSel = Fonts.u8g2_font_inr33_mf;
                break;
            case FONT_INR33_MR:
                fontSel = Fonts.u8g2_font_inr33_mr;
                break;
            case FONT_INR33_MN:
                fontSel = Fonts.u8g2_font_inr33_mn;
                break;
            case FONT_INR33_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_inr33_t_cyrillic;
                break;
            case FONT_INR38_MF:
                fontSel = Fonts.u8g2_font_inr38_mf;
                break;
            case FONT_INR38_MR:
                fontSel = Fonts.u8g2_font_inr38_mr;
                break;
            case FONT_INR38_MN:
                fontSel = Fonts.u8g2_font_inr38_mn;
                break;
            case FONT_INR38_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_inr38_t_cyrillic;
                break;
            case FONT_INR42_MF:
                fontSel = Fonts.u8g2_font_inr42_mf;
                break;
            case FONT_INR42_MR:
                fontSel = Fonts.u8g2_font_inr42_mr;
                break;
            case FONT_INR42_MN:
                fontSel = Fonts.u8g2_font_inr42_mn;
                break;
            case FONT_INR42_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_inr42_t_cyrillic;
                break;
            case FONT_INR46_MF:
                fontSel = Fonts.u8g2_font_inr46_mf;
                break;
            case FONT_INR46_MR:
                fontSel = Fonts.u8g2_font_inr46_mr;
                break;
            case FONT_INR46_MN:
                fontSel = Fonts.u8g2_font_inr46_mn;
                break;
            case FONT_INR46_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_inr46_t_cyrillic;
                break;
            case FONT_INR49_MF:
                fontSel = Fonts.u8g2_font_inr49_mf;
                break;
            case FONT_INR49_MR:
                fontSel = Fonts.u8g2_font_inr49_mr;
                break;
            case FONT_INR49_MN:
                fontSel = Fonts.u8g2_font_inr49_mn;
                break;
            case FONT_INR49_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_inr49_t_cyrillic;
                break;
            case FONT_INR53_MF:
                fontSel = Fonts.u8g2_font_inr53_mf;
                break;
            case FONT_INR53_MR:
                fontSel = Fonts.u8g2_font_inr53_mr;
                break;
            case FONT_INR53_MN:
                fontSel = Fonts.u8g2_font_inr53_mn;
                break;
            case FONT_INR53_T_CYRILLIC:
                fontSel = Fonts.u8g2_font_inr53_t_cyrillic;
                break;
            case FONT_INR57_MN:
                fontSel = Fonts.u8g2_font_inr57_mn;
                break;
            case FONT_INR62_MN:
                fontSel = Fonts.u8g2_font_inr62_mn;
                break;
            case FONT_INB16_MF:
                fontSel = Fonts.u8g2_font_inb16_mf;
                break;
            case FONT_INB16_MR:
                fontSel = Fonts.u8g2_font_inb16_mr;
                break;
            case FONT_INB16_MN:
                fontSel = Fonts.u8g2_font_inb16_mn;
                break;
            case FONT_INB19_MF:
                fontSel = Fonts.u8g2_font_inb19_mf;
                break;
            case FONT_INB19_MR:
                fontSel = Fonts.u8g2_font_inb19_mr;
                break;
            case FONT_INB19_MN:
                fontSel = Fonts.u8g2_font_inb19_mn;
                break;
            case FONT_INB21_MF:
                fontSel = Fonts.u8g2_font_inb21_mf;
                break;
            case FONT_INB21_MR:
                fontSel = Fonts.u8g2_font_inb21_mr;
                break;
            case FONT_INB21_MN:
                fontSel = Fonts.u8g2_font_inb21_mn;
                break;
            case FONT_INB24_MF:
                fontSel = Fonts.u8g2_font_inb24_mf;
                break;
            case FONT_INB24_MR:
                fontSel = Fonts.u8g2_font_inb24_mr;
                break;
            case FONT_INB24_MN:
                fontSel = Fonts.u8g2_font_inb24_mn;
                break;
            case FONT_INB27_MF:
                fontSel = Fonts.u8g2_font_inb27_mf;
                break;
            case FONT_INB27_MR:
                fontSel = Fonts.u8g2_font_inb27_mr;
                break;
            case FONT_INB27_MN:
                fontSel = Fonts.u8g2_font_inb27_mn;
                break;
            case FONT_INB30_MF:
                fontSel = Fonts.u8g2_font_inb30_mf;
                break;
            case FONT_INB30_MR:
                fontSel = Fonts.u8g2_font_inb30_mr;
                break;
            case FONT_INB30_MN:
                fontSel = Fonts.u8g2_font_inb30_mn;
                break;
            case FONT_INB33_MF:
                fontSel = Fonts.u8g2_font_inb33_mf;
                break;
            case FONT_INB33_MR:
                fontSel = Fonts.u8g2_font_inb33_mr;
                break;
            case FONT_INB33_MN:
                fontSel = Fonts.u8g2_font_inb33_mn;
                break;
            case FONT_INB38_MF:
                fontSel = Fonts.u8g2_font_inb38_mf;
                break;
            case FONT_INB38_MR:
                fontSel = Fonts.u8g2_font_inb38_mr;
                break;
            case FONT_INB38_MN:
                fontSel = Fonts.u8g2_font_inb38_mn;
                break;
            case FONT_INB42_MF:
                fontSel = Fonts.u8g2_font_inb42_mf;
                break;
            case FONT_INB42_MR:
                fontSel = Fonts.u8g2_font_inb42_mr;
                break;
            case FONT_INB42_MN:
                fontSel = Fonts.u8g2_font_inb42_mn;
                break;
            case FONT_INB46_MF:
                fontSel = Fonts.u8g2_font_inb46_mf;
                break;
            case FONT_INB46_MR:
                fontSel = Fonts.u8g2_font_inb46_mr;
                break;
            case FONT_INB46_MN:
                fontSel = Fonts.u8g2_font_inb46_mn;
                break;
            case FONT_INB49_MF:
                fontSel = Fonts.u8g2_font_inb49_mf;
                break;
            case FONT_INB49_MR:
                fontSel = Fonts.u8g2_font_inb49_mr;
                break;
            case FONT_INB49_MN:
                fontSel = Fonts.u8g2_font_inb49_mn;
                break;
            case FONT_INB53_MF:
                fontSel = Fonts.u8g2_font_inb53_mf;
                break;
            case FONT_INB53_MR:
                fontSel = Fonts.u8g2_font_inb53_mr;
                break;
            case FONT_INB53_MN:
                fontSel = Fonts.u8g2_font_inb53_mn;
                break;
            case FONT_INB57_MN:
                fontSel = Fonts.u8g2_font_inb57_mn;
                break;
            case FONT_INB63_MN:
                fontSel = Fonts.u8g2_font_inb63_mn;
                break;
            case FONT_LOGISOSO16_TF:
                fontSel = Fonts.u8g2_font_logisoso16_tf;
                break;
            case FONT_LOGISOSO16_TR:
                fontSel = Fonts.u8g2_font_logisoso16_tr;
                break;
            case FONT_LOGISOSO16_TN:
                fontSel = Fonts.u8g2_font_logisoso16_tn;
                break;
            case FONT_LOGISOSO18_TF:
                fontSel = Fonts.u8g2_font_logisoso18_tf;
                break;
            case FONT_LOGISOSO18_TR:
                fontSel = Fonts.u8g2_font_logisoso18_tr;
                break;
            case FONT_LOGISOSO18_TN:
                fontSel = Fonts.u8g2_font_logisoso18_tn;
                break;
            case FONT_LOGISOSO20_TF:
                fontSel = Fonts.u8g2_font_logisoso20_tf;
                break;
            case FONT_LOGISOSO20_TR:
                fontSel = Fonts.u8g2_font_logisoso20_tr;
                break;
            case FONT_LOGISOSO20_TN:
                fontSel = Fonts.u8g2_font_logisoso20_tn;
                break;
            case FONT_LOGISOSO22_TF:
                fontSel = Fonts.u8g2_font_logisoso22_tf;
                break;
            case FONT_LOGISOSO22_TR:
                fontSel = Fonts.u8g2_font_logisoso22_tr;
                break;
            case FONT_LOGISOSO22_TN:
                fontSel = Fonts.u8g2_font_logisoso22_tn;
                break;
            case FONT_LOGISOSO24_TF:
                fontSel = Fonts.u8g2_font_logisoso24_tf;
                break;
            case FONT_LOGISOSO24_TR:
                fontSel = Fonts.u8g2_font_logisoso24_tr;
                break;
            case FONT_LOGISOSO24_TN:
                fontSel = Fonts.u8g2_font_logisoso24_tn;
                break;
            case FONT_LOGISOSO26_TF:
                fontSel = Fonts.u8g2_font_logisoso26_tf;
                break;
            case FONT_LOGISOSO26_TR:
                fontSel = Fonts.u8g2_font_logisoso26_tr;
                break;
            case FONT_LOGISOSO26_TN:
                fontSel = Fonts.u8g2_font_logisoso26_tn;
                break;
            case FONT_LOGISOSO28_TF:
                fontSel = Fonts.u8g2_font_logisoso28_tf;
                break;
            case FONT_LOGISOSO28_TR:
                fontSel = Fonts.u8g2_font_logisoso28_tr;
                break;
            case FONT_LOGISOSO28_TN:
                fontSel = Fonts.u8g2_font_logisoso28_tn;
                break;
            case FONT_LOGISOSO30_TF:
                fontSel = Fonts.u8g2_font_logisoso30_tf;
                break;
            case FONT_LOGISOSO30_TR:
                fontSel = Fonts.u8g2_font_logisoso30_tr;
                break;
            case FONT_LOGISOSO30_TN:
                fontSel = Fonts.u8g2_font_logisoso30_tn;
                break;
            case FONT_LOGISOSO32_TF:
                fontSel = Fonts.u8g2_font_logisoso32_tf;
                break;
            case FONT_LOGISOSO32_TR:
                fontSel = Fonts.u8g2_font_logisoso32_tr;
                break;
            case FONT_LOGISOSO32_TN:
                fontSel = Fonts.u8g2_font_logisoso32_tn;
                break;
            case FONT_LOGISOSO34_TF:
                fontSel = Fonts.u8g2_font_logisoso34_tf;
                break;
            case FONT_LOGISOSO34_TR:
                fontSel = Fonts.u8g2_font_logisoso34_tr;
                break;
            case FONT_LOGISOSO34_TN:
                fontSel = Fonts.u8g2_font_logisoso34_tn;
                break;
            case FONT_LOGISOSO38_TF:
                fontSel = Fonts.u8g2_font_logisoso38_tf;
                break;
            case FONT_LOGISOSO38_TR:
                fontSel = Fonts.u8g2_font_logisoso38_tr;
                break;
            case FONT_LOGISOSO38_TN:
                fontSel = Fonts.u8g2_font_logisoso38_tn;
                break;
            case FONT_LOGISOSO42_TF:
                fontSel = Fonts.u8g2_font_logisoso42_tf;
                break;
            case FONT_LOGISOSO42_TR:
                fontSel = Fonts.u8g2_font_logisoso42_tr;
                break;
            case FONT_LOGISOSO42_TN:
                fontSel = Fonts.u8g2_font_logisoso42_tn;
                break;
            case FONT_LOGISOSO46_TF:
                fontSel = Fonts.u8g2_font_logisoso46_tf;
                break;
            case FONT_LOGISOSO46_TR:
                fontSel = Fonts.u8g2_font_logisoso46_tr;
                break;
            case FONT_LOGISOSO46_TN:
                fontSel = Fonts.u8g2_font_logisoso46_tn;
                break;
            case FONT_LOGISOSO50_TF:
                fontSel = Fonts.u8g2_font_logisoso50_tf;
                break;
            case FONT_LOGISOSO50_TR:
                fontSel = Fonts.u8g2_font_logisoso50_tr;
                break;
            case FONT_LOGISOSO50_TN:
                fontSel = Fonts.u8g2_font_logisoso50_tn;
                break;
            case FONT_LOGISOSO54_TF:
                fontSel = Fonts.u8g2_font_logisoso54_tf;
                break;
            case FONT_LOGISOSO54_TR:
                fontSel = Fonts.u8g2_font_logisoso54_tr;
                break;
            case FONT_LOGISOSO54_TN:
                fontSel = Fonts.u8g2_font_logisoso54_tn;
                break;
            case FONT_LOGISOSO58_TF:
                fontSel = Fonts.u8g2_font_logisoso58_tf;
                break;
            case FONT_LOGISOSO58_TR:
                fontSel = Fonts.u8g2_font_logisoso58_tr;
                break;
            case FONT_LOGISOSO58_TN:
                fontSel = Fonts.u8g2_font_logisoso58_tn;
                break;
            case FONT_LOGISOSO62_TN:
                fontSel = Fonts.u8g2_font_logisoso62_tn;
                break;
            case FONT_LOGISOSO78_TN:
                fontSel = Fonts.u8g2_font_logisoso78_tn;
                break;
            case FONT_LOGISOSO92_TN:
                fontSel = Fonts.u8g2_font_logisoso92_tn;
                break;
            case FONT_PRESSSTART2P_8F:
                fontSel = Fonts.u8g2_font_pressstart2p_8f;
                break;
            case FONT_PRESSSTART2P_8R:
                fontSel = Fonts.u8g2_font_pressstart2p_8r;
                break;
            case FONT_PRESSSTART2P_8N:
                fontSel = Fonts.u8g2_font_pressstart2p_8n;
                break;
            case FONT_PRESSSTART2P_8U:
                fontSel = Fonts.u8g2_font_pressstart2p_8u;
                break;
            case FONT_PCSENIOR_8F:
                fontSel = Fonts.u8g2_font_pcsenior_8f;
                break;
            case FONT_PCSENIOR_8R:
                fontSel = Fonts.u8g2_font_pcsenior_8r;
                break;
            case FONT_PCSENIOR_8N:
                fontSel = Fonts.u8g2_font_pcsenior_8n;
                break;
            case FONT_PCSENIOR_8U:
                fontSel = Fonts.u8g2_font_pcsenior_8u;
                break;
            case FONT_PXPLUSIBMCGATHIN_8F:
                fontSel = Fonts.u8g2_font_pxplusibmcgathin_8f;
                break;
            case FONT_PXPLUSIBMCGATHIN_8R:
                fontSel = Fonts.u8g2_font_pxplusibmcgathin_8r;
                break;
            case FONT_PXPLUSIBMCGATHIN_8N:
                fontSel = Fonts.u8g2_font_pxplusibmcgathin_8n;
                break;
            case FONT_PXPLUSIBMCGATHIN_8U:
                fontSel = Fonts.u8g2_font_pxplusibmcgathin_8u;
                break;
            case FONT_PXPLUSIBMCGA_8F:
                fontSel = Fonts.u8g2_font_pxplusibmcga_8f;
                break;
            case FONT_PXPLUSIBMCGA_8R:
                fontSel = Fonts.u8g2_font_pxplusibmcga_8r;
                break;
            case FONT_PXPLUSIBMCGA_8N:
                fontSel = Fonts.u8g2_font_pxplusibmcga_8n;
                break;
            case FONT_PXPLUSIBMCGA_8U:
                fontSel = Fonts.u8g2_font_pxplusibmcga_8u;
                break;
            case FONT_PXPLUSTANDYNEWTV_8F:
                fontSel = Fonts.u8g2_font_pxplustandynewtv_8f;
                break;
            case FONT_PXPLUSTANDYNEWTV_8R:
                fontSel = Fonts.u8g2_font_pxplustandynewtv_8r;
                break;
            case FONT_PXPLUSTANDYNEWTV_8N:
                fontSel = Fonts.u8g2_font_pxplustandynewtv_8n;
                break;
            case FONT_PXPLUSTANDYNEWTV_8U:
                fontSel = Fonts.u8g2_font_pxplustandynewtv_8u;
                break;
            case FONT_PXPLUSTANDYNEWTV_T_ALL:
                fontSel = Fonts.u8g2_font_pxplustandynewtv_t_all;
                break;
            case FONT_PXPLUSTANDYNEWTV_8_ALL:
                fontSel = Fonts.u8g2_font_pxplustandynewtv_8_all;
                break;
            case FONT_PXPLUSIBMVGA9_TF:
                fontSel = Fonts.u8g2_font_pxplusibmvga9_tf;
                break;
            case FONT_PXPLUSIBMVGA9_TR:
                fontSel = Fonts.u8g2_font_pxplusibmvga9_tr;
                break;
            case FONT_PXPLUSIBMVGA9_TN:
                fontSel = Fonts.u8g2_font_pxplusibmvga9_tn;
                break;
            case FONT_PXPLUSIBMVGA9_MF:
                fontSel = Fonts.u8g2_font_pxplusibmvga9_mf;
                break;
            case FONT_PXPLUSIBMVGA9_MR:
                fontSel = Fonts.u8g2_font_pxplusibmvga9_mr;
                break;
            case FONT_PXPLUSIBMVGA9_MN:
                fontSel = Fonts.u8g2_font_pxplusibmvga9_mn;
                break;
            case FONT_PXPLUSIBMVGA9_T_ALL:
                fontSel = Fonts.u8g2_font_pxplusibmvga9_t_all;
                break;
            case FONT_PXPLUSIBMVGA9_M_ALL:
                fontSel = Fonts.u8g2_font_pxplusibmvga9_m_all;
                break;
            case FONT_PXPLUSIBMVGA8_TF:
                fontSel = Fonts.u8g2_font_pxplusibmvga8_tf;
                break;
            case FONT_PXPLUSIBMVGA8_TR:
                fontSel = Fonts.u8g2_font_pxplusibmvga8_tr;
                break;
            case FONT_PXPLUSIBMVGA8_TN:
                fontSel = Fonts.u8g2_font_pxplusibmvga8_tn;
                break;
            case FONT_PXPLUSIBMVGA8_MF:
                fontSel = Fonts.u8g2_font_pxplusibmvga8_mf;
                break;
            case FONT_PXPLUSIBMVGA8_MR:
                fontSel = Fonts.u8g2_font_pxplusibmvga8_mr;
                break;
            case FONT_PXPLUSIBMVGA8_MN:
                fontSel = Fonts.u8g2_font_pxplusibmvga8_mn;
                break;
            case FONT_PXPLUSIBMVGA8_T_ALL:
                fontSel = Fonts.u8g2_font_pxplusibmvga8_t_all;
                break;
            case FONT_PXPLUSIBMVGA8_M_ALL:
                fontSel = Fonts.u8g2_font_pxplusibmvga8_m_all;
                break;
            case FONT_PX437WYSE700A_TF:
                fontSel = Fonts.u8g2_font_px437wyse700a_tf;
                break;
            case FONT_PX437WYSE700A_TR:
                fontSel = Fonts.u8g2_font_px437wyse700a_tr;
                break;
            case FONT_PX437WYSE700A_TN:
                fontSel = Fonts.u8g2_font_px437wyse700a_tn;
                break;
            case FONT_PX437WYSE700A_MF:
                fontSel = Fonts.u8g2_font_px437wyse700a_mf;
                break;
            case FONT_PX437WYSE700A_MR:
                fontSel = Fonts.u8g2_font_px437wyse700a_mr;
                break;
            case FONT_PX437WYSE700A_MN:
                fontSel = Fonts.u8g2_font_px437wyse700a_mn;
                break;
            case FONT_PX437WYSE700B_TF:
                fontSel = Fonts.u8g2_font_px437wyse700b_tf;
                break;
            case FONT_PX437WYSE700B_TR:
                fontSel = Fonts.u8g2_font_px437wyse700b_tr;
                break;
            case FONT_PX437WYSE700B_TN:
                fontSel = Fonts.u8g2_font_px437wyse700b_tn;
                break;
            case FONT_PX437WYSE700B_MF:
                fontSel = Fonts.u8g2_font_px437wyse700b_mf;
                break;
            case FONT_PX437WYSE700B_MR:
                fontSel = Fonts.u8g2_font_px437wyse700b_mr;
                break;
            case FONT_PX437WYSE700B_MN:
                fontSel = Fonts.u8g2_font_px437wyse700b_mn;
                break;
        }
        return fontSel;
    }

    /**
     * Centralize I2C setup.
     *
     * @param setupType Setup type enum,
     * @param u8g2 Pointer to u8g2_t structure.
     * @param rotation Display rotation.
     * @param byteCb Byte callback pointer.
     * @param gpioAndDelayCb GPIO callback pointer.
     */
    public void setupI2c(final SetupType setupType, final long u8g2, final long rotation, final long byteCb,
            final long gpioAndDelayCb) {
        switch (setupType) {
            case SSD1305_I2C_128X32_NONAME:
                U8g2.setupSsd1305I2c128x32NonameF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1305_I2C_128X32_ADAFRUIT:
                U8g2.setupSsd1305I2c128x32AdafruitF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1305_I2C_128X64_ADAFRUIT:
                U8g2.setupSsd1305I2c128x64AdafruitF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1305_I2C_128X64_RAYSTAR:
                U8g2.setupSsd1305I2c128x64RaystarF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_I2C_128X64_NONAME:
                U8g2.setupSsd1306I2c128x64NonameF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_I2C_128X64_VCOMH0:
                U8g2.setupSsd1306I2c128x64Vcomh0F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_I2C_128X64_ALT0:
                U8g2.setupSsd1306I2c128x64Alt0F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_I2C_72X40_ER:
                U8g2.setupSsd1306I2c72x40ErF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1106_I2C_128X64_NONAME:
                U8g2.setupSh1106I2c128x64NonameF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1106_I2C_128X64_VCOMH0:
                U8g2.setupSh1106I2c128x64Vcomh0F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1106_I2C_128X64_WINSTAR:
                U8g2.setupSh1106I2c128x64WinstarF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1106_I2C_72X40_WISE:
                U8g2.setupSh1106I2c72x40WiseF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1106_I2C_64X32:
                U8g2.setupSh1106I2c64x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1107_I2C_64X128:
                U8g2.setupSh1107I2c64x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1107_I2C_SEEED_96X96:
                U8g2.setupSh1107I2cSeeed96x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1107_I2C_128X80:
                U8g2.setupSh1107I2c128x80F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1107_I2C_128X128:
                U8g2.setupSh1107I2c128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1107_I2C_PIMORONI_128X128:
                U8g2.setupSh1107I2cPimoroni128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1107_I2C_SEEED_128X128:
                U8g2.setupSh1107I2cSeeed128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1108_I2C_160X160:
                U8g2.setupSh1108I2c160x160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1122_I2C_256X64:
                U8g2.setupSh1122I2c256x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_I2C_128X32_UNIVISION:
                U8g2.setupSsd1306I2c128x32UnivisionF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_I2C_128X32_WINSTAR:
                U8g2.setupSsd1306I2c128x32WinstarF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_I2C_102X64_EA_OLEDS102:
                U8g2.setupSsd1306I2c102x64EaOleds102F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SH1106_I2C_128X32_VISIONOX:
                U8g2.setupSh1106I2c128x32VisionoxF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_I2C_64X48_ER:
                U8g2.setupSsd1306I2c64x48ErF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_I2C_48X64_WINSTAR:
                U8g2.setupSsd1306I2c48x64WinstarF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_I2C_64X32_NONAME:
                U8g2.setupSsd1306I2c64x32NonameF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_I2C_64X32_1F:
                U8g2.setupSsd1306I2c64x321fF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1306_I2C_96X16_ER:
                U8g2.setupSsd1306I2c96x16ErF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1309_I2C_128X64_NONAME2:
                U8g2.setupSsd1309I2c128x64Noname2F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1309_I2C_128X64_NONAME0:
                U8g2.setupSsd1309I2c128x64Noname0F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1316_I2C_128X32:
                U8g2.setupSsd1316I2c128x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1317_I2C_96X96:
                U8g2.setupSsd1317I2c96x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1318_I2C_128X96:
                U8g2.setupSsd1318I2c128x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1318_I2C_128X96_XCP:
                U8g2.setupSsd1318I2c128x96XcpF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1325_I2C_NHD_128X64:
                U8g2.setupSsd1325I2cNhd128x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD0323_I2C_OS128064:
                U8g2.setupSsd0323I2cOs128064F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1326_I2C_ER_256X32:
                U8g2.setupSsd1326I2cEr256x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1327_I2C_WS_96X64:
                U8g2.setupSsd1327I2cWs96x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1327_I2C_SEEED_96X96:
                U8g2.setupSsd1327I2cSeeed96x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1327_I2C_EA_W128128:
                U8g2.setupSsd1327I2cEaW128128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1327_I2C_MIDAS_128X128:
                U8g2.setupSsd1327I2cMidas128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1327_I2C_WS_128X128:
                U8g2.setupSsd1327I2cWs128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1327_I2C_VISIONOX_128X96:
                U8g2.setupSsd1327I2cVisionox128x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case LD7032_I2C_60X32:
                U8g2.setupLd7032I2c60x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case LD7032_I2C_60X32_ALT:
                U8g2.setupLd7032I2c60x32AltF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1604_I2C_JLX19264:
                U8g2.setupUc1604I2cJlx19264F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1608_I2C_ERC24064:
                U8g2.setupUc1608I2cErc24064F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1608_I2C_DEM240064:
                U8g2.setupUc1608I2cDem240064F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1608_I2C_ERC240120:
                U8g2.setupUc1608I2cErc240120F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1608_I2C_240X128:
                U8g2.setupUc1608I2c240x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1609_I2C_SLG19264:
                U8g2.setupUc1609I2cSlg19264F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1638_I2C_192X96:
                U8g2.setupUc1638I2c192x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1610_I2C_EA_DOGXL160:
                U8g2.setupUc1610I2cEaDogxl160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1611_I2C_EA_DOGM240:
                U8g2.setupUc1611I2cEaDogm240F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1611_I2C_EA_DOGXL240:
                U8g2.setupUc1611I2cEaDogxl240F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1611_I2C_EW50850:
                U8g2.setupUc1611I2cEw50850F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1611_I2C_CG160160:
                U8g2.setupUc1611I2cCg160160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1611_I2C_IDS4073:
                U8g2.setupUc1611I2cIds4073F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7528_I2C_NHD_C160100:
                U8g2.setupSt7528I2cNhdC160100F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7528_I2C_ERC16064:
                U8g2.setupSt7528I2cErc16064F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1617_I2C_JLX128128:
                U8g2.setupUc1617I2cJlx128128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1601_I2C_128X32:
                U8g2.setupUc1601I2c128x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1601_I2C_128X64:
                U8g2.setupUc1601I2c128x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7567_I2C_64X32:
                U8g2.setupSt7567I2c64x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7567_I2C_HEM6432:
                U8g2.setupSt7567I2cHem6432F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7571_I2C_128X128:
                U8g2.setupSt7571I2c128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7571_I2C_128X96:
                U8g2.setupSt7571I2c128x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7588_I2C_JLX12864:
                U8g2.setupSt7588I2cJlx12864F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75160_I2C_JM16096:
                U8g2.setupSt75160I2cJm16096F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_I2C_JLX256128:
                U8g2.setupSt75256I2cJlx256128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_I2C_WO256X128:
                U8g2.setupSt75256I2cWo256x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_I2C_JLX256160:
                U8g2.setupSt75256I2cJlx256160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_I2C_JLX256160M:
                U8g2.setupSt75256I2cJlx256160mF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_I2C_JLX256160_ALT:
                U8g2.setupSt75256I2cJlx256160AltF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_I2C_JLX240160:
                U8g2.setupSt75256I2cJlx240160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_I2C_JLX25664:
                U8g2.setupSt75256I2cJlx25664F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_I2C_JLX172104:
                U8g2.setupSt75256I2cJlx172104F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_I2C_JLX19296:
                U8g2.setupSt75256I2cJlx19296F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75320_I2C_JLX320240:
                U8g2.setupSt75320I2cJlx320240F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            default:
                throw new RuntimeException(String.format("Setup type %s not supported for I2C", setupType));
        }
    }

    /**
     * Centralize SPI setup.
     *
     * @param setupType Setup type enum,
     * @param u8g2 Pointer to u8g2_t structure.
     * @param rotation Display rotation.
     * @param byteCb Byte callback pointer.
     * @param gpioAndDelayCb GPIO callback pointer.
     */
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
            case SSD1306_72X40_ER:
                U8g2.setupSsd130672x40ErF(u8g2, rotation, byteCb, gpioAndDelayCb);
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
            case SH1107_128X80:
                U8g2.setupSh1107128x80F(u8g2, rotation, byteCb, gpioAndDelayCb);
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
            case SSD1316_128X32:
                U8g2.setupSsd1316128x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
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
            case ST7920_P_256X32:
                U8g2.setupSt7920P256x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7920_256X32:
                U8g2.setupSt7920256x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7920_S_256X32:
                U8g2.setupSt7920S256x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
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
            case HX1230_96X68:
                U8g2.setupHx123096x68F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1604_JLX19264:
                U8g2.setupUc1604Jlx19264F(u8g2, rotation, byteCb, gpioAndDelayCb);
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
            case UC1638_160X128:
                U8g2.setupUc1638160x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case UC1638_192X96:
                U8g2.setupUc1638192x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
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
            case ST7567_64X32:
                U8g2.setupSt756764x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7567_HEM6432:
                U8g2.setupSt7567Hem6432F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7571_128X128:
                U8g2.setupSt7571128x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7571_128X96:
                U8g2.setupSt7571128x96F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7586S_S028HN118A:
                U8g2.setupSt7586sS028hn118aF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7586S_ERC240160:
                U8g2.setupSt7586sErc240160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7586S_YMC240160:
                U8g2.setupSt7586sYmc240160F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST7588_JLX12864:
                U8g2.setupSt7588Jlx12864F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75160_JM16096:
                U8g2.setupSt75160Jm16096F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_JLX256128:
                U8g2.setupSt75256Jlx256128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case ST75256_WO256X128:
                U8g2.setupSt75256Wo256x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
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
            case ST75320_JLX320240:
                U8g2.setupSt75320Jlx320240F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case NT7534_TG12864R:
                U8g2.setupNt7534Tg12864rF(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case IST3020_ERC19264:
                U8g2.setupIst3020Erc19264F(u8g2, rotation, byteCb, gpioAndDelayCb);
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
            case SSD1320_160X32:
                U8g2.setupSsd1320160x32F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1320_160X132:
                U8g2.setupSsd1320160x132F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1322_NHD_256X64:
                U8g2.setupSsd1322Nhd256x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case SSD1322_NHD_128X64:
                U8g2.setupSsd1322Nhd128x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
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
            case RA8835_NHD_240X128:
                U8g2.setupRa8835Nhd240x128F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case RA8835_320X240:
                U8g2.setupRa8835320x240F(u8g2, rotation, byteCb, gpioAndDelayCb);
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
            case S1D15E06_160100:
                U8g2.setupS1d15e06160100F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case S1D15721_240X64:
                U8g2.setupS1d15721240x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case GU800_128X64:
                U8g2.setupGu800128x64F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            case A2PRINTER_384X240:
                U8g2.setupA2printer384x240F(u8g2, rotation, byteCb, gpioAndDelayCb);
                break;
            default:
                throw new RuntimeException(String.format("Setup type %s not supported for SPI", setupType));
        }
    }

    /**
     * Initialize I2C hardware driven display and return pointer to u8g2_t structure.
     *
     * @param setupType Setup type enum,
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
        logger.debug(String.format("Size %d x %d, draw color %d", U8g2.getDisplayWidth(u8g2), U8g2.getDisplayHeight(u8g2), U8g2.
                getDrawColor(u8g2)));
        logger.debug(String.format("Bus 0x%02x, Address %02x", bus, address));
        return u8g2;
    }

    /**
     * Initialize I2C software driven display and return pointer to u8g2_t structure.
     *
     * @param setupType Setup type enum,
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
        logger.debug(String.format("Size %d x %d, draw color %d", U8g2.getDisplayWidth(u8g2), U8g2.getDisplayHeight(u8g2), U8g2.
                getDrawColor(u8g2)));
        logger.debug(String.format("GPIO chip %d, SCL %d, SDA %d, RES %d, Delay %d", gpio, scl, sda, res, delay));
        return u8g2;
    }

    /**
     * Initialize SPI display and return pointer to u8g2_t structure.
     *
     * @param setupType Setup type enum,
     * @param gpio GPIO chip number.
     * @param bus SPI bus number.
     * @param dc DC pin.
     * @param res RESET pin.
     * @param cs CS pin.
     * @return Pointer to u8g2_t structure.
     */
    public long initHwSpi(final SetupType setupType, final int gpio, final int bus, final int dc, final int res, final int cs) {
        final var u8g2 = U8g2.initU8g2();
        setupSpi(setupType, u8g2, U8G2_R0, u8x8_byte_arm_linux_hw_spi, u8x8_arm_linux_gpio_and_delay);
        U8g2.initSpiHw(u8g2, gpio, bus, dc, res, cs);
        U8g2.initDisplay(u8g2);
        logger.debug(String.format("Size %d x %d, draw color %d", U8g2.getDisplayWidth(u8g2), U8g2.getDisplayHeight(u8g2), U8g2.
                getDrawColor(u8g2)));
        logger.debug(String.format("GPIO chip %d, bus 0x%02x, DC %d, RES %d, CS %d", gpio, bus, dc, res, cs));
        return u8g2;
    }

    /**
     * Initialize SPI display and return pointer to u8g2_t structure.
     *
     * @param setupType Setup type enum,
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
        logger.debug(String.format("Size %d x %d, draw color %d", U8g2.getDisplayWidth(u8g2), U8g2.getDisplayHeight(u8g2), U8g2.
                getDrawColor(u8g2)));
        logger.debug(String.format("GPIO chip %d, DC %d, RES %d, MOSI %d, SCK %d, CS %d, Delay %d", gpio, dc, res, mosi, sck, cs,
                delay));
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
     * Free u8g2_t structure from memory, close down GPIO, I2C and SPI.
     *
     * @param u8g2 Pointer to u8g2_t structure.
     */
    public void done(final long u8g2) {
        logger.debug("Done");
        U8g2.doneUserData(u8g2);
        U8g2.done(u8g2);
    }
}
