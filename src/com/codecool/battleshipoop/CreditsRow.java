package com.codecool.battleshipoop;

public class CreditsRow {

    public float progress = 0;
    public String name;
    public String bullsht;

    public CreditsRow()
    {
        this.progress = 0;
        this.name = Util.random(1, 100) >= 50 ? "Ferenc Kárpáti" : "Alex Kovács";
        this.bullsht = FieldPanel.creditsBullsht[Util.random(0, FieldPanel.creditsBullsht.length - 1)];
    }
}
