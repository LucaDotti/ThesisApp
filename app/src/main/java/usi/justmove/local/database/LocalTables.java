package usi.justmove.local.database;

import usi.justmove.local.database.tableHandlers.PAMSurvey;
import usi.justmove.local.database.tableHandlers.PHQ8Survey;
import usi.justmove.local.database.tableHandlers.PSSSurvey;
import usi.justmove.local.database.tableHandlers.PWBSurvey;
import usi.justmove.local.database.tableHandlers.SHSSurvey;
import usi.justmove.local.database.tableHandlers.SWLSSurvey;
import usi.justmove.local.database.tableHandlers.Survey;
import usi.justmove.local.database.tableHandlers.SurveyAlarms;
import usi.justmove.local.database.tableHandlers.SurveyConfig;
import usi.justmove.local.database.tableHandlers.TableHandler;
import usi.justmove.local.database.tableHandlers.User;
import usi.justmove.local.database.tables.AccelerometerTable;
import usi.justmove.local.database.tables.BlueToothTable;
import usi.justmove.local.database.tables.LocationTable;
import usi.justmove.local.database.tables.PAMTable;
import usi.justmove.local.database.tables.PHQ8Table;
import usi.justmove.local.database.tables.PSSTable;
import usi.justmove.local.database.tables.PWBTable;
import usi.justmove.local.database.tables.PhoneCallLogTable;
import usi.justmove.local.database.tables.PhoneLockTable;
import usi.justmove.local.database.tables.SHSTable;
import usi.justmove.local.database.tables.SMSTable;
import usi.justmove.local.database.tables.SWLSTable;
import usi.justmove.local.database.tables.SimpleMoodTable;
import usi.justmove.local.database.tables.SurveyAlarmSurveyTable;
import usi.justmove.local.database.tables.SurveyAlarmsTable;
import usi.justmove.local.database.tables.SurveyConfigTable;
import usi.justmove.local.database.tables.SurveyTable;
import usi.justmove.local.database.tables.UsedAppTable;
import usi.justmove.local.database.tables.UserTable;
import usi.justmove.local.database.tables.WiFiTable;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by usi on 18/01/17.
 */

public enum LocalTables {
    TABLE_ACCELEROMETER(AccelerometerTable.class),
    TABLE_BLUETOOTH(BlueToothTable.class),
    TABLE_LOCATION(LocationTable.class),
    TABLE_CALL_LOG(PhoneCallLogTable.class),
    TABLE_PHONELOCK(PhoneLockTable.class),
    TABLE_SMS(SMSTable.class),
    TABLE_USED_APP(UsedAppTable.class),
    TABLE_WIFI(WiFiTable.class),
    TABLE_PAM(PAMTable.class),
    TABLE_PWB(PWBTable.class),
    TABLE_SIMPLE_MOOD(SimpleMoodTable.class),
    TABLE_PSS(PSSTable.class),
    TABLE_PHQ8(PHQ8Table.class),
    TABLE_SHS(SHSTable.class),
    TABLE_SWLS(SWLSTable.class),
    TABLE_SURVEY(SurveyTable.class),
    TABLE_SURVEY_ALARMS(SurveyAlarmsTable.class),
    TABLE_USER(UserTable.class),
    TABLE_SURVEY_ALARMS_SURVEY(SurveyAlarmSurveyTable.class),
    TABLE_SURVEY_CONFIG(SurveyTable.class);


    LocalTables(Class a) {
    }

    public static TableHandler getTableHandler(LocalTables table) {
        switch(table) {
            case TABLE_PAM:
                return new PAMSurvey(true);
            case TABLE_PWB:
                return new PWBSurvey(true);
            case TABLE_PSS:
                return new PSSSurvey(true);
            case TABLE_PHQ8:
                return new PHQ8Survey(true);
            case TABLE_SHS:
                return new SHSSurvey(true);
            case TABLE_SWLS:
                return new SWLSSurvey(true);
            case TABLE_SURVEY:
                return new Survey(true);
            case TABLE_SURVEY_ALARMS:
                return new SurveyAlarms(true);
            case TABLE_USER:
                return new User(true);
            case TABLE_SURVEY_CONFIG:
                return new SurveyConfig(true);
            default:
                throw new IllegalArgumentException("Table not found!");
        }
    }

    public String getTableName() {
        switch(this) {
            case TABLE_PAM:
                return PAMTable.TABLE_PAM;
            case TABLE_PWB:
                return PWBTable.TABLE_PWB;
            case TABLE_PSS:
                return PSSTable.TABLE_PSS;
            case TABLE_PHQ8:
                return PHQ8Table.TABLE_PHQ8;
            case TABLE_SHS:
                return SHSTable.TABLE_SHS;
            case TABLE_SWLS:
                return SWLSTable.TABLE_SWLS;
            case TABLE_SURVEY:
                return SurveyTable.TABLE_SURVEY;
            case TABLE_SURVEY_ALARMS:
                return SurveyAlarmsTable.TABLE_SURVEY_ALARM;
            case TABLE_USER:
                return UserTable.TABLE_USER;
            case TABLE_SURVEY_ALARMS_SURVEY:
                return SurveyAlarmSurveyTable.TABLE_SURVEY_ALARMS_SURVEY_TABLE;
            case TABLE_SURVEY_CONFIG:
                return SurveyConfigTable.TABLE_SURVEY_CONFIG;
            default:
                throw new IllegalArgumentException("Table not found!");
        }
    }

}
