package usi.memotion.surveys.config;

import usi.memotion.local.database.db.LocalTables;

/**
 * Created by usi on 07/02/17.
 */

public enum SurveyType {
    PAM("pam", false, 1),
    PWB("pwb", false, 2),
    SWLS("swls", false, 4),
    SHS("shs", false, 5),
    PHQ8("phq8", false, 6),
    PSS("pss", false, 7),
    GROUPED_SSPP("grouped_sspp", true, 3, SurveyType.SWLS, SurveyType.SHS, SurveyType.PHQ8, SurveyType.PSS);

    private String surveyName;
    private SurveyType[] surveys;
    private boolean grouped;
    private int id;

    SurveyType(String surveyName, boolean grouped, int id, SurveyType... surveys) {
        this.surveyName = surveyName;
        this.surveys = surveys;
        this.grouped = grouped;
        this.id = id;
    }

    public static SurveyType getSurvey(String surveyName) {
        for (SurveyType s : SurveyType.values()) {
            if (s.surveyName.equals(surveyName)) return s;
        }
        throw new IllegalArgumentException("SurveyType " + surveyName + " not found");
    }

    public static LocalTables[] getSurveyTables(SurveyType survey) {
        int size = survey.surveys.length;

        if(size == 0) {
            size = 1;
        }

        LocalTables[] tables = new LocalTables[size];
        switch(survey) {
            case PAM:
                tables[0] = LocalTables.TABLE_PAM;
                return tables;
            case PWB:
                tables[0] = LocalTables.TABLE_PWB;
                return tables;
            case PHQ8:
                tables[0] = LocalTables.TABLE_PHQ8;
                return tables;
            case PSS:
                tables[0] = LocalTables.TABLE_PSS;
                return tables;
            case SHS:
                tables[0] = LocalTables.TABLE_SHS;
                return tables;
            case SWLS:
                tables[0] = LocalTables.TABLE_SWLS;
                return tables;
            case GROUPED_SSPP:
                for (int i = 0; i < tables.length; i++) {
                    tables[i] = SurveyType.getSurveyTables(survey.surveys[i])[0];
                }
                return tables;
            default:
                throw  new IllegalArgumentException("SurveyType is not grouped!");
        }
    }

    public static LocalTables getSurveyTable(SurveyType survey) {
        LocalTables[] tables = new LocalTables[survey.surveys.length];
        switch(survey) {
            case PAM:
                return LocalTables.TABLE_PAM;
            case PWB:
                return LocalTables.TABLE_PWB;
            case PHQ8:
                return LocalTables.TABLE_PHQ8;
            case PSS:
                return LocalTables.TABLE_PSS;
            case SHS:
                return LocalTables.TABLE_SHS;
            case SWLS:
                return LocalTables.TABLE_SWLS;
            default:
                throw  new IllegalArgumentException("SurveyType is grouped!");
        }
    }

    public SurveyType[] getSurveys() {
        return surveys;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public boolean isGrouped() {
        return grouped;
    }

    public int getId() {
        return id;
    }
}
