package com.bevelio.arcade.misc;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;


public class CC
{
	
	 public static String getLastColors(String input) 
	    {
	        String result = "";
	        int length = input.length();

	        // Search backwards from the end as it is faster
	        for (int index = length - 1; index > -1; index--) {
	            char section = input.charAt(index);
	            if (section == ChatColor.COLOR_CHAR && index < length - 1) {
	                char c = input.charAt(index + 1);
	                ChatColor color = ChatColor.getByChar(c);

	                if (color != null) {
	                    result = color.toString() + result;

	                    // Once we find a color or reset we can stop searching
	                    if (color.isColor() || color.equals(ChatColor.RESET)) {
	                        break;
	                    }
	                }
	            }
	        }

	        return result;
	    }

    public static String
            blue = ChatColor.BLUE.toString(),
            aqua = ChatColor.AQUA.toString(),
            yellow = ChatColor.YELLOW.toString(),
            red = ChatColor.RED.toString(),
            gray = ChatColor.GRAY.toString(),
            gold = ChatColor.GOLD.toString(),
            green = ChatColor.GREEN.toString(),
            white = ChatColor.WHITE.toString(),
            black = ChatColor.BLACK.toString(),

    darkBlue = ChatColor.DARK_BLUE.toString(),
            darkAqua = ChatColor.DARK_AQUA.toString(),
            darkGray = ChatColor.DARK_GRAY.toString(),
            darkGreen = ChatColor.DARK_GREEN.toString(),
            darkPurple = ChatColor.DARK_PURPLE.toString(),
            darkRed = ChatColor.DARK_RED.toString(),

    dBlue = darkBlue,
            dAqua = darkAqua,
            dGray = darkGray,
            dGreen = darkGreen,
            dPurple = darkPurple,
            dRed = darkRed,

    lightPurple = ChatColor.LIGHT_PURPLE.toString(),

    lPurple = lightPurple,

    bold = ChatColor.BOLD.toString(),
            magic = ChatColor.MAGIC.toString(),
            italic = ChatColor.ITALIC.toString(),
            strikeThrough = ChatColor.STRIKETHROUGH.toString(),
            reset = ChatColor.RESET.toString(),

    b = bold,
            m = magic,
            i = italic,
            s = strikeThrough,
            r = reset,

    bBlue = blue + b,
            bAqua = aqua + b,
            bYellow = yellow + b,
            bRed = red + b,
            bGray = gray + b,
            bGold = gold + b,
            bGreen = green + b,
            bWhite = white + b,
            bBlack = black + b,

    bdBlue = dBlue + b,
            bdAqua = dAqua + b,
            bdGray = dGray + b,
            bdGreen = dGreen + b,
            bdPurple = dPurple + b,
            bdRed = dRed + b,

    blPurple = lPurple + b,

    iBlue = blue + i,
            iAqua = aqua + i,
            iYellow = yellow + i,
            iRed = red + i,
            iGray = gray + i,
            iGold = gold + i,
            iGreen = green + i,
            iWhite = white + i,
            iBlack = black + i,

    idBlue = dBlue + i,
            idAqua = dAqua + i,
            idGray = dGray + i,
            idGreen = dGreen + i,
            idPurple = dPurple + i,
            idRed = dRed + i,

    ilPurple = lPurple + i;

    //unicode
    public static String
            peace = "☮",
            flower = "✿",
            plane = "✈",
            sixyNine = "♋",
            death = "☠",
            yinYan = "☯",
            heart = "♥",
            peaceHand = "✌",
            thickCross = "✖",
            nuke = "☢",
            biohazard = "☣",
            medical = "☤",
            bigHeart = "❤",
            sideHeart = "❥",
            leaf = "❦",
            anotherLeaf = "❧",
            hollowHeart = "♡",
            thinItalicCross = "✗",
            ItalicCross = "✘",
            star = "★",
            hollowStar = "☆",
            checkerStar = "✯",
            leftMoon = "☾",
            rightMoon = "☽",
            sun = "☼",
            boldSun = "☀ ",
            cloud = "☁",
            snowman = "☃",
            singleMusicNote = "♪",
            doubleMusicNote = "♫",
            phone = "✆",
            mail = "✉",
            female = "♂",
            male = "♀",
            upTrigle = "▲",
            downTrigle = "▼",
            leftTrigle = "◀",
            rightTrigle = "▶",
            thinItalicTick = "✓",
            italicTick = "✔",

    smiley1 = "☺",
            smiley2 = "ت",
            smiley3 = "ツ",
            smiley4 = "ッ",
            smiley5 = "シ",
            smiley6 = "Ü",
            oneInCircle = "➀",
            twoInCircle = "➁",
            threeInCircle = "➂",
            fourInCircle = "➃",
            fiveInCircle = "➄",
            sixInCircle = "➅",
            sevenInCircle = "➆ ",
            eightInCircle = "➇",
            nineInCircle = "➈",

    arrow = "➨",
            checkerArrow = "➣",
            thickArrow = "➤",

    veryLightShadedBlock = "░",
            lightShadedBlock = "▒",
            shadedBlock = "▓",
            fullyShadedBlock = "█",

    pipeUp = "║",
            pipeUpMidSplit = "╠",
            pipeTopRight = "╔",
            pipeBottomRight = "╚",
            pipeSide = "═",
            pipeTopLeft = "╗",
            pipeBottomLeft = "╝";

    public static List<String> wrapString(String str, int lineLength)
    {
        String[] split = WordUtils.wrap(str, lineLength, null, true).split("\\r\\n");
        String[] fixed = new String[split.length];

        fixed[0] = split[0];

        ArrayList<String> lines = new ArrayList<>();

        for (int i = 1; i < split.length; i++)
        {
            String line = split[i];
            String previous = split[i - 1];


            int code = previous.lastIndexOf("§");

            if (code != -1)
            {
                char cCode = previous.charAt(code == previous.length() - 1 ? code : code + 1);
                if (code == previous.length() - 1)
                {
                    if (ChatColor.getByChar(line.charAt(0)) != null)
                    {
                        fixed[i - 1] = previous.substring(0, previous.length() - 1);
                        line = "§" + line;
                        split[i] = line;
                    }
                } else
                {
                    if (line.length() < 2 || ChatColor.getByChar(line.charAt(1)) == null)
                        if (ChatColor.getByChar(cCode) != null)
                            line = "§" + cCode + line;
                }
            }

            fixed[i] = line;
            split[i] = line;

            lines.add(fixed[i]);
        }
        return lines;
    }
}
