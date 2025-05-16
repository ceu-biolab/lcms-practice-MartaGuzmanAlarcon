package lipid;

import adduct.Adduct;
import adduct.AdductList;

import java.util.*;

/**
 * Class to represent the annotation over a lipid
 */
public class Annotation {

    private static final int PPM_TOLERANCE=5;
    private final Lipid lipid;
    private final double mz;
    private final double intensity; // intensity of the most abundant peak in the groupedPeaks
    private final double rtMin;
    private final IoniationMode ionizationMode;
    private String adduct; // !!TODO The adduct will be detected based on the groupedSignals
    private final Set<Peak> groupedSignals;
    private int score;
    private int totalScoresApplied;


    /**
     * @param lipid
     * @param mz
     * @param intensity
     * @param retentionTime
     * @param ionizationMode
     */
    public Annotation(Lipid lipid, double mz, double intensity, double retentionTime, IoniationMode ionizationMode) {
        this(lipid, mz, intensity, retentionTime, ionizationMode, Collections.emptySet());
    }

    /**
     * @param lipid
     * @param mz
     * @param intensity
     * @param retentionTime
     * @param ionizationMode
     * @param groupedSignals
     */
    public Annotation(Lipid lipid, double mz, double intensity, double retentionTime, IoniationMode ionizationMode, Set<Peak> groupedSignals) {
        this.lipid = lipid;
        this.mz = mz;
        this.rtMin = retentionTime;
        this.intensity = intensity;
        this.ionizationMode = ionizationMode;
        // !!TODO This set should be sorted according to help the program to deisotope the signals plus detect the adduct
        this.groupedSignals = new TreeSet<>(groupedSignals);
        this.score = 0;
        this.totalScoresApplied = 0;
        detectAdduct();
    }

    public Lipid getLipid() {
        return lipid;
    }

    public double getMz() {
        return mz;
    }

    public double getRtMin() {
        return rtMin;
    }

    public String getAdduct() {
        return adduct;
    }

    public void setAdduct(String adduct) {
        this.adduct = adduct;
    }

    public double getIntensity() {
        return intensity;
    }

    public IoniationMode getIonizationMode() {
        return ionizationMode;
    }

    public Set<Peak> getGroupedSignals() {
        return Collections.unmodifiableSet(groupedSignals);
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    // !CHECK Take into account that the score should be normalized between -1 and 1
    public void addScore(int delta) {
        this.score += delta;
        this.totalScoresApplied++;
    }

    /**
     * @return The normalized score between 0 and 1 that consists on the final number divided into the times that the rule
     * has been applied.
     */
    public double getNormalizedScore() {
        System.out.println("totalScoresApplied:"+totalScoresApplied);
        System.out.println("score:"+ this.score);
        return (double) this.score / this.totalScoresApplied;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Annotation)) return false;
        Annotation that = (Annotation) o;
        return Double.compare(that.mz, mz) == 0 &&
                Double.compare(that.rtMin, rtMin) == 0 &&
                Objects.equals(lipid, that.lipid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lipid, mz, rtMin);
    }

    @Override
    public String toString() {
        return String.format("Annotation(%s, mz=%.4f, RT=%.2f, adduct=%s, intensity=%.1f, score=%d)",
                lipid.getName(), mz, rtMin, adduct, intensity, score);
    }


    /**
     * Method to detect an adduct based on reference peak
     */
    public void detectAdduct() {
        double referenceMz = this.mz;

        if (this.ionizationMode == IoniationMode.POSITIVE) {
            for (String candidateAdduct : AdductList.MAPMZPOSITIVEADDUCTS.keySet()) {
                Double referenceMonoisotopicMass = Adduct.getMonoisotopicMassFromMZ(referenceMz, candidateAdduct);

                for (String otherAdduct : AdductList.MAPMZPOSITIVEADDUCTS.keySet()) {
                    if (otherAdduct.equals(candidateAdduct)) continue;

                    for (Peak peak : groupedSignals) {
                        System.out.println("groupedSignals:"+groupedSignals) ;
                        System.out.println("peak:"+peak) ;
                        Double otherPeakMonoIsotopicMass = Adduct.getMonoisotopicMassFromMZ(peak.getMz(), otherAdduct);
                        int error = Adduct.calculatePPMIncrement(referenceMonoisotopicMass, otherPeakMonoIsotopicMass);
                        System.out.println(error + ", " + candidateAdduct + " = " + referenceMonoisotopicMass + ", mz1 = " + referenceMz + ",   " + otherAdduct + " = " + otherPeakMonoIsotopicMass + ", mz2 = " + peak.getMz());
                        System.out.println("error = " + error
                                + ", candidateAdduct = " + candidateAdduct
                                + ", referenceMonoisotopicMass = " + referenceMonoisotopicMass
                                + ", referenceMz = " + referenceMz
                                + ", otherAdduct = " + otherAdduct
                                + ", otherPeakMonoIsotopicMass = " + otherPeakMonoIsotopicMass
                                + ", mz2 = " + peak.getMz());


                        if (error < PPM_TOLERANCE) {
                            this.adduct = candidateAdduct;

                            return;
                        }

                    }
                }
            }
        } else if (this.ionizationMode == IoniationMode.NEGATIVE) {
            for (String candidateAdduct : AdductList.MAPMZNEGATIVEADDUCTS.keySet()) {
                Double referenceMonoisotopicMass = Adduct.getMonoisotopicMassFromMZ(referenceMz, candidateAdduct);

                for (String otherAdduct : AdductList.MAPMZNEGATIVEADDUCTS.keySet()) {
                    if (otherAdduct.equals(candidateAdduct)) continue;

                    for (Peak peak : groupedSignals) {
                        Double otherPeakMonoIsotopicMass = Adduct.getMonoisotopicMassFromMZ(peak.getMz(), otherAdduct);

                        int error = Adduct.calculatePPMIncrement(referenceMonoisotopicMass, otherPeakMonoIsotopicMass);

                        System.out.println("error = " + error
                                + ", candidateAdduct = " + candidateAdduct
                                + ", referenceMonoisotopicMass = " + referenceMonoisotopicMass
                                + ", referenceMz = " + referenceMz
                                + ", otherAdduct = " + otherAdduct
                                + ", otherPeakMonoIsotopicMass = " + otherPeakMonoIsotopicMass
                                + ", mz2 = " + peak.getMz());

                        if (error < PPM_TOLERANCE) {
                            this.adduct = candidateAdduct;
                            return;
                        }
                    }
                }
            }
        }
    }

}
