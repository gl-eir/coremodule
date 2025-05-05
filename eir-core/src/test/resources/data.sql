
INSERT INTO `ALLOWED_TAC` (`sno`, `tac`) VALUES (1, '11111111');
INSERT INTO `ALLOWED_TAC` (`sno`, `tac`) VALUES (2, '22222222');
INSERT INTO `ALLOWED_TAC` (`sno`, `tac`) VALUES (3, '12345678');
INSERT INTO `ALLOWED_TAC` (`sno`, `tac`) VALUES (4, '87654321');

--INSERT INTO `BLACK_LIST` (`sno`, `IMEI_ESN_MEID`, `IMEI`, `MSISDN`, `ACTUAL_IMEI`) VALUES(1, '123456789123456', '123456789123456', '9818504927', '123456789123456');
--INSERT INTO `BLACK_LIST` (`sno`, `IMEI_ESN_MEID`, `IMEI`, `MSISDN`, `ACTUAL_IMEI`) VALUES(2, '123456789654321', '123456789654321', '9818504928', '123456789654321');

INSERT INTO `BLOCKED_TAC` (`sno`, `tac`) VALUES(1, '33333333');
INSERT INTO `BLOCKED_TAC` (`sno`, `tac`) VALUES(2, '44444444');
INSERT INTO `BLOCKED_TAC` (`sno`, `tac`) VALUES(3, '44443333');
INSERT INTO `BLOCKED_TAC` (`sno`, `tac`) VALUES(4, '33334444');

INSERT INTO `configuration_call_flag` (`sno`, `name`, `flag`) VALUES(1, 'eir_workflow_global', 'on');
INSERT INTO `configuration_call_flag` (`sno`, `name`, `flag`) VALUES(2, 'eir_devicetype_global', 'on');
INSERT INTO `configuration_call_flag` (`sno`, `name`, `flag`) VALUES(3, 'device_type_specific', 'on');
INSERT INTO `configuration_call_flag` (`sno`, `name`, `flag`) VALUES(4, 'device_type_imei_validation', 'off');
INSERT INTO `configuration_call_flag` (`sno`, `name`, `flag`) VALUES(5, 'eir_blockedlist', 'on');
INSERT INTO `configuration_call_flag` (`sno`, `name`, `flag`) VALUES(6, 'eir_trackedlist', 'on');
INSERT INTO `configuration_call_flag` (`sno`, `name`, `flag`) VALUES(7, 'eir_exceptionlist', 'on');
INSERT INTO `configuration_call_flag` (`sno`, `name`, `flag`) VALUES(8, 'eir_blockedtaclist', 'on');
INSERT INTO `configuration_call_flag` (`sno`, `name`, `flag`) VALUES(9, 'eir_allowedtaclist', 'on');





