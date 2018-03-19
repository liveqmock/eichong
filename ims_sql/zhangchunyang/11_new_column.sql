ALTER TABLE `tbl_powerstation`
ADD COLUMN `type`  int(4) NULL DEFAULT 0 COMMENT '1.公交场站 2. 详情咨询产品' AFTER `status`;

ALTER TABLE `tbl_purchasehistory`
MODIFY COLUMN `puHi_Type`  int(11) NULL DEFAULT NULL COMMENT ' 1.充电消费 2.停车费  3.信用还款  4.充值 5.发票快递费  6.溢缴款 7.资金划入 8.授信 11.充电消费退款 12.停车费退款 13.信用还款退款 14.充值退款 15.发票快递费退款  16.溢缴款领回 17.资金划出  ' AFTER `pk_PurchaseHistory`;


DELIMITER ;;
CREATE PROCEDURE `proc_update_user_status`(cpyId bigint(4), state int(4))
BEGIN
    IF state = 1 THEN
						UPDATE tbl_user tuser,tbl_user_normal normal
						set tuser.user_status = 2
						WHERE tuser.user_id = normal.user_id
						and normal.cpy_id = cpyId;

						UPDATE tbl_user tuser,tbl_user_company userC
						set tuser.user_status = 2
						WHERE tuser.user_id = userC.user_id
						and userC.cpy_id = cpyId;

						UPDATE tbl_user tuser,tbl_usercard card
						set tuser.user_status = 2
						WHERE tuser.user_id = card.user_Id
						and card.cpy_id = cpyId;

            UPDATE tbl_user tuser,tbl_user_admin admin
						set tuser.user_status = 2
						WHERE tuser.user_id = admin.user_Id
						and admin.cpy_id = cpyId;

            
				ELSE
						UPDATE tbl_user tuser,tbl_user_normal normal
						set tuser.user_status = 1
						WHERE tuser.user_id = normal.user_id
						and normal.cpy_id = cpyId;

						UPDATE tbl_user tuser,tbl_user_company userC
						set tuser.user_status = 1
						WHERE tuser.user_id = userC.user_id
						and userC.cpy_id = cpyId;

						UPDATE tbl_user tuser,tbl_usercard card
						set tuser.user_status = 1
						WHERE tuser.user_id = card.user_Id
						and card.cpy_id = cpyId;

            UPDATE tbl_user tuser,tbl_user_admin admin
						set tuser.user_status = 1
            WHERE tuser.user_id = admin.user_Id
						and admin.cpy_id = cpyId;

		END IF;
END;;
DELIMITER ;


DELETE FROM tbl_user WHERE user_leval in (1,2);
insert into tbl_user (user_account,user_password,user_leval,user_status,gmt_create,gmt_modified) VALUE ('admin01','e10adc3949ba59abbe56e057f20f883e',1,1,NOW(),NOW());

update tbl_usercard set uc_type = 10 WHERE uc_type != 12;