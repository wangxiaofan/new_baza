package com.baza.android.bzw.businesscontroller.message;

public class IMResumeBean {

    public IMResumeBean(String type, Data data) {
        this.type = type;
        this.data = data;
    }

    String type;//: "jobShare",
    Data data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        public Data(String resumeId, String candidateName, String title, String company, String cityName, int yearOfExperience, int degree, String school) {
            this.resumeId = resumeId;
            this.candidateName = candidateName;
            this.title = title;
            this.company = company;
            this.cityName = cityName;
            this.yearOfExperience = yearOfExperience;
            this.degree = degree;
            this.school = school;
        }

        String resumeId;// "",  //候选人简历id
        String candidateName;//: "",  //候选人姓名
        String title;//: "",  //候选人当前职位
        String company;//: "",  //候选人当前公司
        String cityName;//: "",  //所在城市
        int yearOfExperience;//: 0,  //工作经验
        int degree;//: 0,  //学历，
        String school;//: ""  //毕业院校

        public String getResumeId() {
            return resumeId;
        }

        public void setResumeId(String resumeId) {
            this.resumeId = resumeId;
        }

        public String getCandidateName() {
            return candidateName;
        }

        public void setCandidateName(String candidateName) {
            this.candidateName = candidateName;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public int getYearOfExperience() {
            return yearOfExperience;
        }

        public void setYearOfExperience(int yearOfExperience) {
            this.yearOfExperience = yearOfExperience;
        }

        public int getDegree() {
            return degree;
        }

        public void setDegree(int degree) {
            this.degree = degree;
        }

        public String getSchool() {
            return school;
        }

        public void setSchool(String school) {
            this.school = school;
        }
    }
}
