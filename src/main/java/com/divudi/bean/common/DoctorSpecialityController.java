/*
 * Open Hospital Management Information System
 *
 * Dr M H B Ariyaratne
 * Acting Consultant (Health Informatics)
 * (94) 71 5812399
 * (94) 71 5812399
 */
package com.divudi.bean.common;

import com.divudi.entity.DoctorSpeciality;
import com.divudi.entity.Vocabulary;
import com.divudi.facade.DoctorSpecialityFacade;
import com.divudi.facade.util.JsfUtil;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, MSc, MD(Health Informatics)
 * Acting Consultant (Health Informatics)
 */
@Named
@SessionScoped
public class DoctorSpecialityController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;

    @EJB
    DoctorSpecialityFacade ejbFacade;

    List<DoctorSpeciality> selectedItems;
    private DoctorSpeciality current;
    private List<DoctorSpeciality> items = null;
    String selectText = "";

    DoctorSpeciality selected;

    public DoctorSpeciality getSelected() {
        return selected;
    }

    public void setSelected(DoctorSpeciality selected) {
        this.selected = selected;
    }

    public void deleteSelected(){
        if(selected==null){
            JsfUtil.addErrorMessage("Nothing to delete");
            return;
        }
        selected.setRetired(true);
        selected.setRetiredAt(new Date());
        selected.setRetirer(getSessionController().getLoggedUser());
        getFacade().edit(selected);
        items = null;
    }
    
    public void prepareCreate(){
        selected = new DoctorSpeciality();
    }
    
    public void updateSelected() {
        if (getSelected() == null) {
            JsfUtil.addErrorMessage("Nothing to save");
            return;
        }
        if (getSelected().getId() == null || getSelected().getId() == 0) {
            getFacade().create(selected);
        } else {
            getFacade().edit(selected);
        }
        items = null;
        getItems();
    }

    public List<DoctorSpeciality> completeSpeciality(String qry) {
        //   ////System.out.println("qry = " + qry);
        List<DoctorSpeciality> lst;
        lst = getFacade().findByJpql("select c from DoctorSpeciality c where c.retired=false and (c.name) like '%" + qry.toUpperCase() + "%' order by c.name");
        //   ////System.out.println("lst = " + lst);
        return lst;
    }

    public List<DoctorSpeciality> getSelectedItems() {
        if (selectText ==null || selectText.trim().equals("") ) {
            selectedItems = getFacade().findByJpql("select c from DoctorSpeciality c where c.retired=false order by c.name");
        } else {
            selectedItems = getFacade().findByJpql("select c from DoctorSpeciality c where c.retired=false and (c.name) like '%" + getSelectText().toUpperCase() + "%' order by c.name");
        }

        return selectedItems;
    }

    public void prepareAdd() {
        current = new DoctorSpeciality();
    }

    public void setSelectedItems(List<DoctorSpeciality> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public String getSelectText() {
        return selectText;
    }

    private void recreateModel() {
        items = null;
    }

    public void saveSelected() {

        if (getCurrent().getId() != null && getCurrent().getId() > 0) {
            getFacade().edit(current);
            UtilityController.addSuccessMessage("Updated Successfully.");
        } else {
            current.setCreatedAt(new Date());
            current.setCreater(getSessionController().getLoggedUser());
            getFacade().create(current);
            UtilityController.addSuccessMessage("Saved Successfully");
        }
        recreateModel();
        getItems();
    }

    public void setSelectText(String selectText) {
        this.selectText = selectText;
    }

    public DoctorSpecialityFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(DoctorSpecialityFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public DoctorSpecialityController() {
    }

    public DoctorSpeciality getCurrent() {
        if (current == null) {
            current = new DoctorSpeciality();
        }
        return current;
    }

    public void setCurrent(DoctorSpeciality current) {
        this.current = current;
    }
    
    // Method to generate the Excel file and initiate the download
    public void downloadAsExcel() {
        getItems();
        try {
            // Create a new Excel workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Doctor Speciality Data");

            // Create a header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Speciality Name");
            headerRow.createCell(1).setCellValue("Income Name");
            // Add more columns as needed

            // Populate the data rows
            int rowNum = 1;
            for (DoctorSpeciality speciality : items) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(speciality.getName());
                row.createCell(1).setCellValue(speciality.getDescription());
            }

            // Set the response headers to initiate the download
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"doctor_speciality_data.xlsx\"");

            // Write the workbook to the response output stream
            workbook.write(response.getOutputStream());
            workbook.close();
            context.responseComplete();
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
        }
    }

    public void delete() {

        if (current != null) {
            current.setRetired(true);
            current.setRetiredAt(new Date());
            current.setRetirer(getSessionController().getLoggedUser());
            getFacade().edit(current);
            UtilityController.addSuccessMessage("Deleted Successfully");
        } else {
            UtilityController.addSuccessMessage("Nothing to Delete");
        }
        recreateModel();
        getItems();
        current = null;
        getCurrent();
    }

    private DoctorSpecialityFacade getFacade() {
        return ejbFacade;
    }

    public List<DoctorSpeciality> getItems() {
        if (items == null) {
            String j;
            j="select s from DoctorSpeciality s where s.retired=false order by s.name";
            items = getFacade().findByJpql(j);
        }
        return items;
    }

    /**
     *
     */
    @FacesConverter(forClass = DoctorSpeciality.class)
    public static class DoctorSpecialityControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DoctorSpecialityController controller = (DoctorSpecialityController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "doctorSpecialityController");
            return controller.getEjbFacade().find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof DoctorSpeciality) {
                DoctorSpeciality o = (DoctorSpeciality) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + DoctorSpecialityController.class.getName());
            }
        }
    }

    /**
     *
     */
   
}