import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;

public class Main {
    public static void main(String[] args) {
        buildRegion();
        buildAccesories();
        buildEnemy();
    }


    public static void improveAcces() {
        //Zur aufbereitung der Accesorie Daten
        StringBuilder s = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader("src/XenobladeAccesories.CSV"))) {
            while (br.ready())
                s.append(br.readLine()).append("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String[] data = s.toString().split("\n");
        String prev = "AAAA";
        for (int i = 1; i < data.length; i++) {
            String[] lineData = data[i].split(";");
            if (lineData[1].length() != 0) {
                System.out.println(data[i]);
                prev = lineData[1];
            } else {
                System.out.println(lineData[0] + ";" + prev + ";" + lineData[2] + ";" + lineData[3] + ";" + lineData[4]);
            }
        }
    }

    private static void buildAccesories() {
        StringBuilder s = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader("src/XenobladeAccesories.CSV"))) {
            while (br.ready())
                s.append(br.readLine()).append("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String s1 = s.toString().replaceAll("'", "''");
        s1.replace("N/A", "NULL");
        s1.replace("YES", "NULL");
        String[] data = s1.split("\n");

        LinkedList<String>[] rarities = new LinkedList[3];
        rarities[0] = new LinkedList<>();
        rarities[1] = new LinkedList<>();
        rarities[2] = new LinkedList<>();
        //Filter Rarities
        for (int i = 1; i < data.length; i++) {
            String[] lineData = data[i].split(";");
            if (!rarities[0].contains(lineData[2]) && !lineData[2].equals("N/A"))
                rarities[0].add(lineData[2]);
            if (!rarities[1].contains(lineData[3]) && !lineData[3].equals("N/A"))
                rarities[1].add(lineData[3]);
            if (!rarities[2].contains(lineData[4]) && !lineData[4].equals("N/A"))
                rarities[2].add(lineData[4]);
        }
        // Insert Rarities
        for (int i = 0; i < data.length; i++) {
            if (rarities[0].size() > 0) {
                String in = rarities[0].removeFirst();
                if (!in.equals("NULL"))
                    System.out.println("INSERT INTO rarity(rarity, strength) VALUES('Legendary','" + in + "');");
            }

            if (rarities[1].size() > 0) {
                String in = rarities[1].removeFirst();
                if (!in.equals("NULL"))

                    System.out.println("INSERT INTO rarity(rarity, strength) VALUES('Rare','" + in + "');");
            }
            if (rarities[2].size() > 0) {
                String in = rarities[2].removeFirst();
                if (!in.equals("NULL"))
                    System.out.println("INSERT INTO rarity(rarity, strength) VALUES('Common','" + in + "');");
            }
        }

        //Insert Accesories and assign rarities
        for (int i = 1; i < data.length; i++) {
            String[] lineData = data[i].split(";");
            System.out.println("INSERT INTO accessory (aname, effect) VALUES('" + lineData[0] + "','" + lineData[1] + "');");
            if (lineData[2].length() > 0 && !lineData[2].equals("N/A") && !lineData[2].equals("NULL"))
                System.out.println("INSERT INTO rel_accessory_rarity (rid," + "aname) VALUES(" +
                        "(SELECT rid FROM rarity WHERE rarity= 'Legendary' AND strength='" + lineData[2] + "')" +
                        ",'" + lineData[0] + "');");
            if (lineData[3].length() > 0 && !lineData[3].equals("N/A") && !lineData[3].equals("NULL"))
                System.out.println("INSERT INTO rel_accessory_rarity (rid," + "aname) VALUES(" +
                        "(SELECT rid FROM rarity WHERE rarity= 'Rare' AND strength='" + lineData[3] + "')" +
                        ",'" + lineData[0] + "');");
            if (lineData[4].length() > 0 && !lineData[4].equals("N/A") && !lineData[4].equals("NULL"))
                System.out.println("INSERT INTO rel_accessory_rarity (rid," + "aname) VALUES(" +
                        "\n(SELECT rid FROM rarity WHERE rarity= 'Common' AND strength='" + lineData[4] + "')" +
                        ",'" + lineData[0] + "');");

        }
    }

    private static void buildEnemy() {

        StringBuilder s = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader("src/XenobladeUniqueMonster.CSV"))) {
            while (br.ready())
                s.append(br.readLine()).append("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String s1 = s.toString().replaceAll("'", "''").replaceAll("\\(CRL\\)","").replaceAll("\\[Repeatable]","").replaceAll("\\(C\\)","");
        String[] data = s1.split("\n");
        HashSet<String> loot=new HashSet<String>();

        //Insert loot
        for (String value : data) {
            String[] linedata = value.split(";");
            String[] itemdata = linedata[9].split(",");
            for (String itemdatum : itemdata) {
                loot.add(itemdatum.trim());
            }
            loot.add(linedata[7].trim());
        }
        for (String looti: loot){
            if(looti.equals("---")|| looti.isEmpty())
                continue;
            System.out.println("INSERT INTO loot (name) VALUES('"+looti+"');");
        }

        //Insert Levels
        TreeSet<Integer> levelset = new TreeSet<>();
        for (String datum : data) {
            String[] linedata = datum.split(";");
            String[] leveldata = linedata[2].split(",");
            for (String leveldatum : leveldata) {
                levelset.add(Integer.parseInt(leveldatum.trim()));
            }
        }
        for (Integer i : levelset
        ) {
            System.out.println("INSERT INTO level(level) VALUES(" + i + ");");
        }

        //Generate Enemies
        for (String dat : data) {
            String[] linedata = dat.split(";");
            boolean suberboss = false;
            String[] levels = linedata[2].split(",");
            if (levels.length > 1)
                suberboss = true;

            System.out.println("INSERT INTO elite_enemy(ename, species, enemytype, gemtype, superboss, subregion, fasttravelpoint) VALUES('" + linedata[1].trim() + "','" + linedata[3].trim() + "','" + linedata[4].trim() + "','" + linedata[10].trim() + "'," + suberboss + ",'" + linedata[5].trim() + "','" + linedata[12].trim() + "');");
            String[] levela = linedata[2].split(",");
            String[] golds = linedata[11].split(",");
            // Levels
            for (int j = 0; j < levela.length; j++) {
                System.out.println("INSERT INTO rel_enemy_level(level, ename, gold) VALUES(" + levela[j].trim() + ",'" + linedata[1].trim() + "'" + "," + golds[j].trim() + ");");
            }
            //Loot
            String[] enemyLoot = linedata[9].split(",");
            for (String value : enemyLoot) {
                if(value.trim().length()<1)
                    continue;
                System.out.println("INSERT INTO rel_enemy_loot (ename, lname, firsttime) VALUES('" + linedata[1].trim() + "','" + value.trim() + "',false);");
            }
            System.out.println("INSERT INTO rel_enemy_loot (ename, lname, firsttime) VALUES('" + linedata[1].trim() + "','" + linedata[7].trim() + "',true);");

            //Accessories
            String[] acc = linedata[8].replaceAll("\\(L\\)", "").split(",");
            for (String value : acc) {
                if(!value.trim().equals("N/A"))
                System.out.println("INSERT INTO rel_enemy_accessory (ename, aname) VALUES('" + linedata[1].trim() + "','" + value.trim() + "');");
            }
        }
    }

    public static void buildRegion() {
        StringBuilder s = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader("src/input.csv"))) {
            while (br.ready())
                s.append(br.readLine()).append("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String s1 = s.toString().replaceAll("'", "''");
        String[] data = s1.split("\n");
        int i;
        HashSet<String> sub=new HashSet<String>();
        //Make Subregion
        for (int j = 1; j <data.length ; j++) {

            String[] linedata= data[j].split(";");
            if(linedata.length<2){
                j++;
                continue;
            }
            sub.add(linedata[2]);

        }
        System.out.println("INSERT INTO region (rname) VALUES ('test');" );
        for (String subs: sub) {
            System.out.println("INSERT INTO subregion (sname, rname) VALUES('"+subs+"','"+"test"+"');");
        }


        for (i = 1; i < data.length; i++) {
            String[] linedata = data[i].split(";");
            if (linedata.length < 3)
                break;
            System.out.println("INSERT INTO location (lname, subregion) VALUES('" + linedata[1] + "','" + linedata[2] + "');");
        }
        i+=2;




        for (; i < data.length; i++) {
            String[] linedata = data[i].split(";");
            if (linedata.length < 3)
                break;
            System.out.println("INSERT INTO fastTravel (tname, subregion, secret, camp) VALUES('" + linedata[1] + "','" + linedata[2] + "', false, false);");
        }
        i+=2;
        //Secret
        for (; i < data.length; i++) {
            String[] linedata = data[i].split(";");
            if (linedata.length < 3 || linedata[0].equals("No"))
                break;
            System.out.println("INSERT INTO fasttravel (tname, subregion, secret, camp) VALUES('" + linedata[1] + "','" + linedata[2] + "',true, false);");

        }
        i+=2;

        //landmark
        for (; i < data.length; i++) {
            String[] linedata = data[i].split(";");
            if (linedata.length < 3 || linedata[0].equals("No"))
                break;
            System.out.println("INSERT INTO fasttravel (tname, subregion, secret, camp) VALUES('" + linedata[1] + "','" + linedata[2] + "',false, true);");

        }

    }
}
