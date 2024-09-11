package io.mosip.registration.controller.eodapproval;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationClientStatusCode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.controller.BaseController;
import io.mosip.registration.controller.vo.RegistrationApprovalVO;
import io.mosip.registration.dto.mastersync.ReasonListDto;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.sync.MasterSyncService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_APPROVAL_CONTROLLER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

/**
 *
 * {@code ApprovalController} is the controller class for approval of packets
 *
 * @author Jivesh Kumar
 */
@Controller
public class ApprovalController extends BaseController implements Initializable {
    /**
     * Stage
     */
    private static Stage approvalPrimaryStage;

    /**
     * Instance of {@link Logger}
     */
    private static final Logger LOGGER = AppConfig.getLogger(ApprovalController.class);

    @Autowired
    private MasterSyncService masterSyncService;

    @Autowired
    private RegistrationApprovalController registrationApprovalController;

    /**
     * Combobox for approval reason
     */
    @FXML
    private ComboBox<String> approvalComboBox;

    /**
     * Button for Submit
     */
    @FXML
    private Button approvalSubmit;

    /** The approval map list. */
    private static List<Map<String, String>> approvalMapList;

    /** The approval registration data. */
    private static RegistrationApprovalVO approvalRegData;

    /** The approval table. */
    private static TableView<RegistrationApprovalVO> regApprovalTable;

    private static ObservableList<RegistrationApprovalVO> observableList;
    private static Map<String, Integer> packetIds = new HashMap<>();

    private static String controllerName;

    @FXML
    private Button closeButton;

    /*
     * (non-Javadoc)
     *
     * @see javafx.fxml.Initializable#initialize(java.net.URL,
     * java.util.ResourceBundle)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.info(LOG_REG_APPROVAL_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Page loading has been started");
        approvalSubmit.disableProperty().set(true);
        approvalComboBox.getItems().clear();
        List<ReasonListDto> reasonList;
        try {
            reasonList = masterSyncService.getAllReasonsList(applicationContext.getApplicationLanguage());
            closeButton.setGraphic(new ImageView(new Image(
                    this.getClass().getResourceAsStream(RegistrationConstants.CLOSE_IMAGE_PATH), 20, 20, true, true)));
            approvalComboBox.setItems(FXCollections
                    .observableArrayList(reasonList.stream().filter(reason -> "APR".equals(reason.getRsnCatCode())).map(list -> list.getName()).collect(Collectors.toList())));
            disableColumnsReorder(regApprovalTable);
        } catch (RegBaseCheckedException exRegBaseCheckedException) {
            LOGGER.error(LOG_REG_APPROVAL_CONTROLLER, APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
                    exRegBaseCheckedException.getMessage() + ExceptionUtils.getStackTrace(exRegBaseCheckedException));
        }
        LOGGER.info(LOG_REG_APPROVAL_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Page loading has been ended");
    }


    public static void initData(RegistrationApprovalVO regData, Map<String, Integer> packets, Stage stage,
                                List<Map<String, String>> mapList, ObservableList<RegistrationApprovalVO> oList,
                                TableView<RegistrationApprovalVO> table, String controller) {
        approvalRegData = regData;
        packetIds = packets;
        approvalPrimaryStage = stage;
        approvalMapList = mapList;
        observableList = oList;
        regApprovalTable = table;
        controllerName = controller;
    }

    /**
     * {@code updatePacketStatus} is for updating packet status to approved
     *
     * @throws RegBaseCheckedException
     */
    public void packetUpdateStatus() throws RegBaseCheckedException {
        LOGGER.info(LOG_REG_APPROVAL_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
                "Packet updation as approval has been started");

        for (Map<String, String> registrationMap : approvalMapList) {
            if (registrationMap.containsValue(approvalRegData.getId())) {
                approvalMapList.remove(registrationMap);
                break;
            }
        }

        Map<String, String> map = new WeakHashMap<>();
        map.put(RegistrationConstants.PACKET_APPLICATION_ID, approvalRegData.getId());
        map.put(RegistrationConstants.PACKET_ID, approvalRegData.getPacketId());
        map.put(RegistrationConstants.STATUSCODE, RegistrationClientStatusCode.APPROVED.getCode());
        map.put(RegistrationConstants.STATUSCOMMENT, approvalComboBox.getSelectionModel().getSelectedItem());
        approvalMapList.add(map);

        approvalSubmit.disableProperty().set(true);
        approvalPrimaryStage.close();

        if (controllerName.equals(RegistrationConstants.EOD_PROCESS_REGISTRATIONAPPROVALCONTROLLER)) {

            int focusedIndex = regApprovalTable.getSelectionModel().getFocusedIndex();

            int rowNum = packetIds.get(regApprovalTable.getSelectionModel().getSelectedItem().getPacketId());
            RegistrationApprovalVO approvalDTO = new RegistrationApprovalVO(
                    regApprovalTable.getSelectionModel().getSelectedItem().getSlno(),
                    regApprovalTable.getSelectionModel().getSelectedItem().getId(),
                    regApprovalTable.getSelectionModel().getSelectedItem().getPacketId(),
                    regApprovalTable.getSelectionModel().getSelectedItem().getDate(),
                    regApprovalTable.getSelectionModel().getSelectedItem().getAcknowledgementFormPath(),
                    regApprovalTable.getSelectionModel().getSelectedItem().getOperatorId(),
                    new Image(getImagePath(RegistrationConstants.TICK_IMG, true)),
                    regApprovalTable.getSelectionModel().getSelectedItem().getHasBwords());

            observableList.set(rowNum, approvalDTO);
            registrationApprovalController.wrapListAndAddFiltering(observableList);
            regApprovalTable.requestFocus();
            regApprovalTable.getFocusModel().focus(focusedIndex);
            regApprovalTable.getSelectionModel().select(focusedIndex);
            LOGGER.info(LOG_REG_APPROVAL_CONTROLLER, APPLICATION_NAME, APPLICATION_ID,
                    "Packet updation as approval has been ended");
        }
    }

    /**
     * {@code approvalWindowExit} handles the event when the user clicks the close button or exits the
     * approval reason popup window. It logs the closure event and closes the approval window.
     *
     */
@FXML
    public void approvalWindowExit() {
        LOGGER.info(LOG_REG_APPROVAL_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, "Approval Popup window is closed");
        approvalPrimaryStage.close();
    }

    /**
     * Approval combobox action.
     *
     * @param
     */
    public void approvalComboboxAction() {
        approvalSubmit.disableProperty().set(false);
    }
}
