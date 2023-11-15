package pl.lbiio.quickadoption.data

data class PublicAnnouncementDetails(
    var announcementId: Long = -1L,
    var ownerID: String = "",
    var dateRange: String = "",
    var food: String = "",
    var animalImage: String = "",
    var animalDescription: String = "",
    var ownerDescription: String = "",
    var ownerImage: String = "",
    var country: String = "",
    var city: String = ""
){
    fun clearObject(){
        this.announcementId= -1L
        this.ownerID = ""
        this.dateRange = ""
        this.food = ""
        this.animalImage = ""
        this.animalDescription = ""
        this.ownerDescription = ""
        this.ownerImage = ""
        this.country = ""
        this.city = ""
    }
}
