package adduct;

import adduct.AdductList;

public class Adduct {


    /**
     * Calculate the mass to search depending on the adduct hypothesis
     *
     * @param mz     mz
     * @param adduct adduct name ([M+H]+, [2M+H]+, [M+2H]2+, etc..)
     * @return the monoisotopic mass of the experimental mass mz with the adduct @param adduct
     */
    public static Double getMonoisotopicMassFromMZ(Double mz, String adduct) {
        if (mz == null || adduct == null) return null;

        // Extraer carga y multímero
        int multimer = 1;
        int charge = 1;

        java.util.regex.Matcher multimerMatcher = java.util.regex.Pattern.compile("^\\[(\\d*)M").matcher(adduct);
        java.util.regex.Matcher chargeMatcher = java.util.regex.Pattern.compile("([0-9]*)(\\+|−|\\-)\\]?$").matcher(adduct);

        if (multimerMatcher.find()) {
            String multimerStr = multimerMatcher.group(1);
            if (!multimerStr.isEmpty()) {
                multimer = Integer.parseInt(multimerStr);
            }
        }

        if (chargeMatcher.find()) {
            String chargeStr = chargeMatcher.group(1);
            if (!chargeStr.isEmpty()) {
                charge = Integer.parseInt(chargeStr);
            }
        }

        // Buscar masa del aducto
        Double adductMass = null;
        if (AdductList.MAPMZPOSITIVEADDUCTS.containsKey(adduct)) {
            adductMass = AdductList.MAPMZPOSITIVEADDUCTS.get(adduct);
        } else if (AdductList.MAPMZNEGATIVEADDUCTS.containsKey(adduct)) {
            adductMass = AdductList.MAPMZNEGATIVEADDUCTS.get(adduct);
        }

        if (adductMass == null) return null;

        // Calcular la masa monoisotópica
       double monoMass = ((mz * charge) + adductMass) / multimer;

        return monoMass;
    }


    /**
     * Calculate the mz of a monoisotopic mass with the corresponding adduct
     *
     * @param monoisotopicMass
     * @param adduct           adduct name ([M+H]+, [2M+H]+, [M+2H]2+, etc..)
     * @return
     */
    public static Double getMZFromMonoisotopicMass(Double monoisotopicMass, String adduct) {
        if (monoisotopicMass == null || adduct == null) return null;

        // Extraer carga y multímero
        int multimer = 1;
        int charge = 1;

        // Regex para extraer el número de multímero
        java.util.regex.Matcher multimerMatcher = java.util.regex.Pattern.compile("^\\[(\\d*)M").matcher(adduct);
        // Regex para extraer el número de carga
        java.util.regex.Matcher chargeMatcher = java.util.regex.Pattern.compile("([0-9]*)(\\+|−|\\-)\\]?$").matcher(adduct);

        if (multimerMatcher.find()) {
            String multimerStr = multimerMatcher.group(1);
            if (!multimerStr.isEmpty()) {
                multimer = Integer.parseInt(multimerStr);
            }
        }

        if (chargeMatcher.find()) {
            String chargeStr = chargeMatcher.group(1);
            if (!chargeStr.isEmpty()) {
                charge = Integer.parseInt(chargeStr);
            }
        }

        // Buscar masa del aducto
        Double adductMass = null;
        if (AdductList.MAPMZPOSITIVEADDUCTS.containsKey(adduct)) {
            adductMass = AdductList.MAPMZPOSITIVEADDUCTS.get(adduct);
        } else if (AdductList.MAPMZNEGATIVEADDUCTS.containsKey(adduct)) {
            adductMass = AdductList.MAPMZNEGATIVEADDUCTS.get(adduct);
        }

        if (adductMass == null) return null;

        // Calcular el m/z

        double mz = (monoisotopicMass * multimer - adductMass) / charge;

        return mz;
    }


    /**
     * Returns the ppm difference between measured mass and theoretical mass
     *
     * @param experimentalMass Mass measured by MS
     * @param theoreticalMass  Theoretical mass of the compound
     */
    public static int calculatePPMIncrement(Double experimentalMass, Double theoreticalMass) {

        System.out.println("experimentalMass:" + experimentalMass);
        System.out.println("theoreticalMass:" + theoreticalMass);
        int ppmIncrement;
        ppmIncrement = (int) Math.round(Math.abs((experimentalMass - theoreticalMass) * 1000000
                / theoreticalMass));
        return ppmIncrement;
    }

    /**
     * Returns the ppm difference between measured mass and theoretical mass
     *
     * @param experimentalMass Mass measured by MS
     * @param ppm              ppm of tolerance
     */
    public static double calculateDeltaPPM(Double experimentalMass, int ppm) {
        double deltaPPM;
        deltaPPM = Math.round(Math.abs((experimentalMass * ppm) / 1000000));
        return deltaPPM;
    }
  /* public static void main(String[] args) {
        Double mz = 518.2000;
        String adduct = "[M+2H]2+";  // Aducto con doble carga

        Double result = getMonoisotopicMassFromMZ(mz, adduct);
        System.out.println("Monoisotopic mass for m/z " + mz + " and adduct " + adduct + ": " + result);

        // Resultado esperado: el valor de la masa monoisotópica para el aducto con doble carga
    } */
}







