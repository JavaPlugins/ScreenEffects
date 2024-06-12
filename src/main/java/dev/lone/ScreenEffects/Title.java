package dev.lone.ScreenEffects;

public class Title
{
    public String message;
    public String image;
    public int fadein;
    public final int stay;
    public final int fadeout;

    public Title(String message, String image, int fadein, int stay, int fadeout)
    {
        this.message = message;
        this.image = image;
        this.fadein = fadein;
        this.stay = stay;
        this.fadeout = fadeout;
    }
}
